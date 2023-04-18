package com.ts.visualranks.configuration.implementation;

import com.ts.visualranks.configuration.ReloadableConfig;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
import java.util.List;

public class InventoriesConfiguration implements ReloadableConfig {

    @Description({ " ", "# Visual ranks inventory" })
    public VisualRanksInventory visualRanksInventory = new VisualRanksInventory();

    @Contextual
    public static class VisualRanksInventory {
        public String title = "&8Visual ranks";

        public int rows = 6;

        public int pageSize = 45;

        public ItemConfiguration visualRankItem = new ItemConfiguration(
                "&e{RANK}",
                List.of(
                        "&7Price: &e{PRICE}",
                        "&7Preview: {RANK}&7 {PLAYER}: the best plugin!",
                        "",
                        "&7Click to buy!"
                ),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.GRAY_DYE,
                false
        );

        public ItemConfiguration boughtVisualRankItem = new ItemConfiguration(
                "&e{RANK}",
                List.of(
                        "&7Preview: {RANK}&7 {PLAYER}: the best plugin",
                        "",
                        "&7Click to change!"
                ),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.LIME_DYE,
                false
        );

        public ItemConfiguration currentVisualRankItem = new ItemConfiguration(
                "&e{RANK}",
                List.of(
                        "&7Preview: {RANK}&7 {PLAYER}: the best plugin!",
                        "",
                        "&7Click to set!"
                ),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.LIGHT_BLUE_DYE,
                true
        );

        public ItemConfigurationSlot previousPageItem = new ItemConfigurationSlot(
                47,
                "&ePrevious page",
                List.of("&7Click to go previous page!"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.ARROW,
                false
        );

        public ItemConfigurationSlot nextPageItem = new ItemConfigurationSlot(
                51,
                "&eNext page",
                List.of("&7Click to go next page!"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.ARROW,
                false
        );

        public ItemConfigurationSlot quitItem = new ItemConfigurationSlot(
                49,
                "&cClose",
                List.of("&7Click to close gui!"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.BARRIER,
                false
        );

        public ItemConfigurationSlot resetCurrentRank = new ItemConfigurationSlot(
                53,
                "&eReset rank",
                List.of("&7Click to reset ranks!"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.BARREL,
                false
        );

    }

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "inventories.yml");
    }

}