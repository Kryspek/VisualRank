package com.ts.visualranks.visualrank.purchase;

import com.ts.visualranks.configuration.implementation.VisualRankConfiguration;
import com.ts.visualranks.configuration.implementation.VisualRankItem;
import com.ts.visualranks.visualrank.VisualRank;
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

    public void purchase(VisualUser user, VisualRankItem visualRank) {
        Player player = this.server.getPlayer(user.getUniqueId());

        this.addVisualTransaction(user, visualRank);

        this.economy.withdrawPlayer(player, visualRank.getPrice());
    }

    public void addVisualTransaction(VisualUser user, VisualRank visualRank) {
        UUID userUuid = user.getUniqueId();

        VisualTransaction transaction = new VisualTransaction(userUuid, visualRank.getName());
        user.addTransaction(transaction);
    }

    public boolean canPurchase(VisualUser user, VisualRankItem visualRank) {
        UUID userUuid = user.getUniqueId();
        Player player = this.server.getPlayer(userUuid);

        return this.economy.has(player, visualRank.getPrice());
    }

}