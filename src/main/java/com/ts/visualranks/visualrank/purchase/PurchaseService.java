package com.ts.visualranks.visualrank.purchase;

import com.ts.visualranks.configuration.implementation.VisualRankConfiguration;
import com.ts.visualranks.visualrank.transaction.VisualTransaction;
import com.ts.visualranks.visualrank.user.VisualUser;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PurchaseService {

    private final Economy economy;
    private final Server server;

    public PurchaseService(Economy economy, Server server) {
        this.economy = economy;
        this.server = server;
    }

    public void purchase(VisualUser user, VisualRankConfiguration visualRank) {
        UUID userUuid = user.getUniqueId();
        Player player = this.server.getPlayer(userUuid);

        VisualTransaction transaction = new VisualTransaction(userUuid, visualRank.getName());
        user.addTransaction(transaction);

        this.economy.withdrawPlayer(player, visualRank.getPrice());
    }

    public boolean canPurchase(VisualUser user, VisualRankConfiguration visualRank) {
        UUID userUuid = user.getUniqueId();
        Player player = this.server.getPlayer(userUuid);

        return this.economy.has(player, visualRank.getPrice());
    }

}