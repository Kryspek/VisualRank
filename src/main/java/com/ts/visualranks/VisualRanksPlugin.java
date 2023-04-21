package com.ts.visualranks;

import com.ts.visualranks.command.InvalidUsage;
import com.ts.visualranks.command.PermissionMessage;
import com.ts.visualranks.command.implementation.VisualRanksCommand;
import com.ts.visualranks.configuration.ConfigurationManager;
import com.ts.visualranks.configuration.implementation.InventoriesConfiguration;
import com.ts.visualranks.configuration.implementation.MessageConfiguration;
import com.ts.visualranks.configuration.implementation.PluginConfiguration;
import com.ts.visualranks.configuration.implementation.VisualRanksConfiguration;
import com.ts.visualranks.database.DatabaseManager;
import com.ts.visualranks.database.wrapper.VisualUserOrmLite;
import com.ts.visualranks.hook.HookManager;
import com.ts.visualranks.hook.implementation.LuckPermsHook;
import com.ts.visualranks.hook.implementation.VaultHook;
import com.ts.visualranks.hook.implementation.placeholderapi.PlaceholderApiController;
import com.ts.visualranks.hook.implementation.placeholderapi.PlaceholderApiHook;
import com.ts.visualranks.notification.NotificationAnnouncer;
import com.ts.visualranks.scheduler.BukkitSchedulerImpl;
import com.ts.visualranks.scheduler.Scheduler;
import com.ts.visualranks.util.LegacyColorProcessor;
import com.ts.visualranks.visualrank.VisualRankInventory;
import com.ts.visualranks.visualrank.VisualRankManager;
import com.ts.visualranks.visualrank.purchase.PurchaseService;
import com.ts.visualranks.visualrank.user.VisualUserRepository;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class VisualRanksPlugin extends JavaPlugin {

    private Scheduler scheduler;

    private ConfigurationManager configurationManager;
    private DatabaseManager databaseManager;

    private PluginConfiguration pluginConfiguration;
    private VisualRanksConfiguration visualRanksConfiguration;
    private InventoriesConfiguration inventoriesConfiguration;
    private MessageConfiguration messageConfiguration;

    private VisualUserRepository visualUserRepository;

    private VisualRankManager visualRankManager;

    private HookManager hookManager;

    private LuckPerms luckPerms;

    private Economy economy;

    private EventCaller eventCaller;

    private PurchaseService purchaseService;

    private AudienceProvider audienceProvider;
    private MiniMessage miniMessage;
    private NotificationAnnouncer notificationAnnouncer;

    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        Stopwatch started = Stopwatch.createStarted();
        Server server = this.getServer();
        Logger logger = this.getLogger();

        this.scheduler = new BukkitSchedulerImpl(this);

        this.configurationManager = new ConfigurationManager(this.getDataFolder());

        this.pluginConfiguration = this.configurationManager.load(new PluginConfiguration());
        this.visualRanksConfiguration = this.configurationManager.load(new VisualRanksConfiguration());
        this.inventoriesConfiguration = this.configurationManager.load(new InventoriesConfiguration());
        this.messageConfiguration = this.configurationManager.load(new MessageConfiguration());

        try {
            this.databaseManager = new DatabaseManager(this.pluginConfiguration, logger, this.getDataFolder());
            this.databaseManager.connect();

            this.visualUserRepository = VisualUserOrmLite.create(this.databaseManager, this.scheduler);
        } catch (Exception exception) {
            exception.printStackTrace();

            logger.severe("Can't connect to database! Disabling plugin...");

            server.getPluginManager().disablePlugin(this);
        }

        this.visualRankManager = new VisualRankManager(this.configurationManager, this.visualRanksConfiguration, this.visualRanksConfiguration);

        this.hookManager = new HookManager(this);

        LuckPermsHook luckPermsHook = new LuckPermsHook(server);
        this.hookManager.initialize(luckPermsHook);
        this.luckPerms = luckPermsHook.getLuckPerms();

        VaultHook vaultHook = new VaultHook(server);
        this.hookManager.initialize(vaultHook);
        this.economy = vaultHook.getEconomy();

        PlaceholderApiHook placeholderApiHook = new PlaceholderApiHook(this.visualUserRepository, this.visualRankManager, this.luckPerms);
        this.hookManager.initialize(placeholderApiHook);

        this.eventCaller = new EventCaller(server);

        this.purchaseService = new PurchaseService(this.economy, server);

        this.audienceProvider = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.builder()
                .postProcessor(new LegacyColorProcessor())
                .build();
        this.notificationAnnouncer = new NotificationAnnouncer(this.audienceProvider, this.miniMessage);

        this.liteCommands = LiteBukkitFactory.builder(server, "visual-ranks")
                .argument(Player.class, new BukkitPlayerArgument<>(this.getServer(), this.messageConfiguration.wrongUsage.onlyForPlayer))

                .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>(this.messageConfiguration.wrongUsage.onlyForPlayer))

                .commandInstance(new VisualRanksCommand(new VisualRankInventory(
                        this.scheduler,
                        this.miniMessage,
                        this.visualUserRepository,
                        this.visualRankManager,
                        this.eventCaller,
                        this.luckPerms, this.purchaseService,
                        this.inventoriesConfiguration,
                        this.messageConfiguration,
                        this.notificationAnnouncer
                ), this.notificationAnnouncer, this.messageConfiguration, this.configurationManager, visualUserRepository, purchaseService, server))

                .invalidUsageHandler(new InvalidUsage(this.notificationAnnouncer, this.messageConfiguration))
                .permissionHandler(new PermissionMessage(this.messageConfiguration, this.notificationAnnouncer))

                .register();

        Stream.of(
                new PlaceholderApiController(placeholderApiHook.getVisualUserCache())
        ).forEach(plugin -> this.getServer().getPluginManager().registerEvents(plugin, this));

        new Metrics(this, 18235);

        long elapsed = started.elapsed().toMillis();
        this.getLogger().info("Successfully loaded VisualRanks in " + elapsed + "ms");
    }

    @Override
    public void onDisable() {
        if (this.audienceProvider != null) {
            this.audienceProvider.close();
        }

        if (this.liteCommands != null) {
            this.liteCommands.getPlatform().unregisterAll();
        }
    }

}