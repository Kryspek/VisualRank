package com.ts.visualranks.hook.implementation;

import com.ts.visualranks.hook.Hook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook implements Hook {

    private final Server server;
    private Economy economy;

    public VaultHook(Server server) {
        this.server = server;
    }

    @Override
    public void initialize() {
        RegisteredServiceProvider<Economy> economyProvider = this.server.getServicesManager().getRegistration(Economy.class);

        if (economyProvider == null) {
            throw new IllegalStateException("Economy provider is null");
        }

        this.economy = economyProvider.getProvider();
    }

    @Override
    public String pluginName() {
        return "Vault";
    }

    public Economy getEconomy() {
        return this.economy;
    }

}