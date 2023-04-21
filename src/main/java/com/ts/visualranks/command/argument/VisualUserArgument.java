package com.ts.visualranks.command.argument;

import com.ts.visualranks.configuration.implementation.MessageConfiguration;
import com.ts.visualranks.visualrank.user.VisualUser;
import com.ts.visualranks.visualrank.user.VisualUserRepository;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import panda.std.Result;

import java.util.concurrent.TimeUnit;

@ArgumentName("user")
public class VisualUserArgument implements OneArgument<VisualUser> {

    private final VisualUserRepository visualUserRepository;
    private final MessageConfiguration messageConfiguration;
    private final Server server;

    public VisualUserArgument(VisualUserRepository visualUserRepository, MessageConfiguration messageConfiguration, Server server) {
        this.visualUserRepository = visualUserRepository;
        this.messageConfiguration = messageConfiguration;
        this.server = server;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Result<VisualUser, ?> parse(LiteInvocation invocation, String argument) {
        OfflinePlayer offlinePlayer = this.server.getOfflinePlayer(argument);

        if (offlinePlayer == null) {
            return Result.error(this.messageConfiguration.wrongUsage.cantFindPlayer);
        }

        return Result.ok(this.visualUserRepository.findUser(offlinePlayer.getUniqueId())
                .orTimeout(15, TimeUnit.SECONDS)
                .join());
    }
}
