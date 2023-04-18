package com.ts.visualranks.hook.implementation;

import com.ts.visualranks.hook.Hook;
import net.luckperms.api.LuckPerms;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsHook implements Hook {

    private final Server server;
    private LuckPerms luckPerms;

    public LuckPermsHook(Server server) {
        this.server = server;
    }

    @Override
    public void initialize() {
        RegisteredServiceProvider<LuckPerms> luckPermsProvider = this.server.getServicesManager().getRegistration(LuckPerms.class);

        if (luckPermsProvider == null) {
            throw new IllegalStateException("LuckPerms provider is null");
        }

        this.luckPerms = luckPermsProvider.getProvider();
    }

    @Override
    public String pluginName() {
        return "LuckPerms";
    }

    public LuckPerms getLuckPerms() {
        return this.luckPerms;
    }

}