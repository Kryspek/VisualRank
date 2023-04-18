package com.ts.visualranks.command.handler.implementation;

import com.ts.visualranks.configuration.ConfigurationManager;
import com.ts.visualranks.configuration.implementation.MessageConfiguration;
import com.ts.visualranks.notification.NotificationAnnouncer;
import com.ts.visualranks.visualrank.VisualRankInventory;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;

@Route(name = "visualranks", aliases = {"vr", "visualrank", "visualranks"})
public class VisualRanksCommand {

    private final VisualRankInventory visualRankInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final ConfigurationManager configurationManager;

    public VisualRanksCommand(VisualRankInventory visualRankInventory, NotificationAnnouncer notificationAnnouncer, MessageConfiguration messageConfiguration, ConfigurationManager configurationManager) {
        this.visualRankInventory = visualRankInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.configurationManager = configurationManager;
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

}