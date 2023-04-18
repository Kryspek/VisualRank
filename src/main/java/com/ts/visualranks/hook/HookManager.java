package com.ts.visualranks.hook;

import com.ts.visualranks.VisualRanksPlugin;
import org.bukkit.plugin.PluginManager;

public class HookManager {

    private final VisualRanksPlugin plugin;

    public HookManager(VisualRanksPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize(Hook hook) {
        PluginManager pluginManager = this.plugin.getServer().getPluginManager();

        if (pluginManager.isPluginEnabled(hook.pluginName())) {
            hook.initialize();

            System.out.println("Hooked into " + hook.pluginName());
        }
    }

}