package com.ts.visualranks.command.handler;

import com.ts.visualranks.configuration.implementation.MessageConfiguration;
import com.ts.visualranks.notification.NotificationAnnouncer;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.handle.PermissionHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionMessage implements PermissionHandler<CommandSender> {

    private final MessageConfiguration messageConfiguration;
    private final NotificationAnnouncer messageAnnouncer;

    public PermissionMessage(MessageConfiguration messageConfiguration, NotificationAnnouncer messageAnnouncer) {
        this.messageConfiguration = messageConfiguration;
        this.messageAnnouncer = messageAnnouncer;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, RequiredPermissions requiredPermissions) {
        Player player = (Player) sender;

        this.messageAnnouncer.sendMessage(player, this.messageConfiguration.wrongUsage.noPermission);
    }

}