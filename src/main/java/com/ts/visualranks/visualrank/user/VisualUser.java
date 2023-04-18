package com.ts.visualranks.visualrank.user;

import com.ts.visualranks.visualrank.transaction.VisualTransaction;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class VisualUser {

    private final UUID uniqueId;
    private String currentRank = "";
    private final Map<String, VisualTransaction> transactions;

    public VisualUser(UUID uniqueId, List<VisualTransaction> transactions) {
        this.uniqueId = uniqueId;
        this.transactions = transactions
                .stream()
                .collect(Collectors.toMap(VisualTransaction::getRankName, visualTransaction -> visualTransaction));
    }

    public boolean hasRank(String rankName) {
        return this.transactions.containsKey(rankName);
    }

    public void setCurrentRank(String visualRank) {
        this.currentRank = visualRank;
    }

    public void addTransaction(VisualTransaction visualTransaction) {
        this.transactions.put(visualTransaction.getRankName(), visualTransaction);
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getCurrentRank() {
        return this.currentRank;
    }

    public Collection<VisualTransaction> getTransactions() {
        return Collections.unmodifiableCollection(this.transactions.values());
    }

}