package com.ts.visualranks.visualrank;

import com.ts.visualranks.EventCaller;
import com.ts.visualranks.configuration.implementation.InventoriesConfiguration;
import com.ts.visualranks.configuration.implementation.ItemConfiguration;
import com.ts.visualranks.configuration.implementation.ItemConfigurationSlot;
import com.ts.visualranks.configuration.implementation.MessageConfiguration;
import com.ts.visualranks.configuration.implementation.VisualRankConfiguration;
import com.ts.visualranks.notification.NotificationAnnouncer;
import com.ts.visualranks.scheduler.Scheduler;
import com.ts.visualranks.util.Legacy;
import com.ts.visualranks.visualrank.purchase.PurchaseService;
import com.ts.visualranks.visualrank.user.VisualUser;
import com.ts.visualranks.visualrank.user.VisualUserRepository;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class VisualRankInventory {

    private final Scheduler scheduler;
    private final MiniMessage miniMessage;
    private final VisualUserRepository visualUserRepository;
    private final VisualRankManager visualRankManager;
    private final EventCaller eventCaller;
    private final LuckPerms luckPerms;
    private final PurchaseService purchaseService;
    private final InventoriesConfiguration inventoriesConfiguration;
    private final MessageConfiguration messageConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;

    public VisualRankInventory(
            Scheduler scheduler,
            MiniMessage miniMessage,
            VisualUserRepository visualUserRepository,
            VisualRankManager visualRankManager,
            EventCaller eventCaller,
            LuckPerms luckPerms, PurchaseService purchaseService,
            InventoriesConfiguration inventoriesConfiguration,
            MessageConfiguration messageConfiguration,
            NotificationAnnouncer notificationAnnouncer) {
        this.scheduler = scheduler;
        this.miniMessage = miniMessage;
        this.visualUserRepository = visualUserRepository;
        this.visualRankManager = visualRankManager;
        this.eventCaller = eventCaller;
        this.luckPerms = luckPerms;
        this.purchaseService = purchaseService;
        this.inventoriesConfiguration = inventoriesConfiguration;
        this.messageConfiguration = messageConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    public void openInventory(Player player) {
        this.scheduler.async(() -> {
            InventoriesConfiguration.VisualRanksInventory visualRanksInventory = this.inventoriesConfiguration.visualRanksInventory;
            MessageConfiguration.VisualRankSection visualRankSection = this.messageConfiguration.visualRankSection;

            PaginatedGui gui = Gui.paginated()
                    .title(Legacy.RESET_ITALIC.append(this.miniMessage.deserialize(visualRanksInventory.title)))
                    .rows(visualRanksInventory.rows)
                    .pageSize(visualRanksInventory.pageSize)
                    .disableAllInteractions()
                    .create();

            ItemConfiguration visualRankItem = visualRanksInventory.visualRankItem;
            ItemConfiguration boughtRankItem = visualRanksInventory.boughtVisualRankItem;
            ItemConfiguration currentRankItem = visualRanksInventory.currentVisualRankItem;

            this.setItem(gui, visualRanksInventory.nextPageItem, event -> gui.next());
            this.setItem(gui, visualRanksInventory.previousPageItem, event -> gui.previous());
            this.setItem(gui, visualRanksInventory.quitItem, event -> player.closeInventory());

            UUID uuid = player.getUniqueId();
            CompletableFuture<VisualUser> completableUser = this.visualUserRepository.findUser(uuid);

            VisualUser user = completableUser
                    .orTimeout(15, TimeUnit.SECONDS)
                    .join();

            VisualRankSetEvent visualRankSetEvent = new VisualRankSetEvent(uuid);

            this.setItem(gui, visualRanksInventory.resetCurrentRank, event -> {
                this.notificationAnnouncer.sendMessage(player, visualRankSection.resetVisualRank);

                user.setCurrentRank("");

                this.visualUserRepository.update(user).whenComplete((aVoid, updateError) -> {
                    if (updateError != null) {
                        updateError.printStackTrace();
                        return;
                    }

                    this.eventCaller.callEvent(visualRankSetEvent);
                });

                gui.close(player);
            });

            for (VisualRankConfiguration visualRank : this.visualRankManager.getVisualRanks()) {
                Formatter formatter = new Formatter()
                        .register("{RANK}", this.getUserGroupPrefix(player) + visualRank.getName())
                        .register("{PRICE}", visualRank.getPrice())
                        .register("{PLAYER}", player.getName());

                if (user.hasRank(visualRank.getName())) {
                    if (user.getCurrentRank() != null && user.getCurrentRank().equalsIgnoreCase(visualRank.getName())) {
                        gui.addItem(currentRankItem.asGuiItem(event -> {
                            this.notificationAnnouncer.sendMessage(player, visualRankSection.alreadyEquipped);

                            gui.close(player);
                        }, formatter));

                        continue;
                    }

                    gui.addItem(boughtRankItem.asGuiItem(event -> {
                        this.notificationAnnouncer.sendMessage(player, formatter.format(visualRankSection.equipRank));

                        user.setCurrentRank(visualRank.getName());

                        this.visualUserRepository.update(user).whenComplete((aVoid, updateError) -> {
                            if (updateError != null) {
                                updateError.printStackTrace();
                                return;
                            }

                            this.eventCaller.callEvent(visualRankSetEvent);
                        });

                        gui.close(player);
                    }, formatter));

                    continue;
                }

                gui.addItem(visualRankItem.asGuiItem(event -> {
                    if (!this.purchaseService.canPurchase(user, visualRank)) {
                        this.notificationAnnouncer.sendMessage(player, formatter.format(visualRankSection.notEnoughMoney));
                        return;
                    }

                    this.purchaseService.purchase(user, visualRank);
                    this.visualUserRepository.update(user);

                    this.notificationAnnouncer.sendMessage(player, formatter.format(visualRankSection.purchased));

                    gui.close(player);
                }, formatter));
            }


            this.scheduler.sync(() -> gui.open(player));
        });
    }

    private void setItem(PaginatedGui gui, ItemConfigurationSlot item, GuiAction<InventoryClickEvent> action) {
        gui.setItem(item.slot, item.asGuiItem(action));
    }

    private String getUserGroupPrefix(Player player) {
        CachedMetaData metaData = this.luckPerms.getPlayerAdapter(Player.class).getMetaData(player);

        String prefix = metaData.getPrefix();

        if (prefix == null) {
            return "&7";
        }

        return prefix.substring(0, 2);
    }

}