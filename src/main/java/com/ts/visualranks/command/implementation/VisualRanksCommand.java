package com.ts.visualranks.command.implementation;

import com.ts.visualranks.configuration.ConfigurationManager;
import com.ts.visualranks.configuration.implementation.MessageConfiguration;
import com.ts.visualranks.notification.NotificationAnnouncer;
import com.ts.visualranks.visualrank.VisualRank;
import com.ts.visualranks.visualrank.VisualRankInventory;
import com.ts.visualranks.visualrank.purchase.PurchaseService;
import com.ts.visualranks.visualrank.user.VisualUser;
import com.ts.visualranks.visualrank.user.VisualUserRepository;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Route(name = "visualranks", aliases = {"vr", "visualrank", "visualranks"})
public class VisualRanksCommand {

    private final VisualRankInventory visualRankInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final ConfigurationManager configurationManager;
    private final VisualUserRepository visualUserRepository;
    private final PurchaseService purchaseService;
    private final Server server;

    public VisualRanksCommand(VisualRankInventory visualRankInventory, NotificationAnnouncer notificationAnnouncer, MessageConfiguration messageConfiguration, ConfigurationManager configurationManager, VisualUserRepository visualUserRepository, PurchaseService purchaseService, Server server) {
        this.visualRankInventory = visualRankInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.configurationManager = configurationManager;
        this.visualUserRepository = visualUserRepository;
        this.purchaseService = purchaseService;
        this.server = server;
    }

    @Execute(required = 0)
    @Permission("visualranks.openinventory")
    void execute(Player player) {
        this.visualRankInventory.openInventory(player);
    }

    @Execute(route = "reload", required = 0)
    @Permission("visualranks.reload")
    void reload(Player player) {
        this.configurationManager.reload();

        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.visualRankSection.reloadConfiguration);
    }

    @Execute(route = "add", required = 2)
    @Permission("visualranks.add")
    void add(CommandSender sender, @Arg VisualUser user, @Arg VisualRank rank) {
        this.purchaseService.addVisualTransaction(user, rank);
        this.visualUserRepository.update(user);

        OfflinePlayer offlinePlayer = this.server.getOfflinePlayer(user.getUniqueId());

        this.notificationAnnouncer.sendMessage(sender, this.messageConfiguration.visualRankSection.addVisualRank
                .replace("{RANK}", rank.getName())
                .replace("{PLAYER}", offlinePlayer.getName()));

        this.notificationAnnouncer.sendMessage(user.getUniqueId(), this.messageConfiguration.visualRankSection.recivedVisualRank.replace("{RANK}", rank.getName()));
    }
}