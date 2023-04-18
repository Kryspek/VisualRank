package com.ts.visualranks.visualrank.transaction;

import java.time.Instant;
import java.util.UUID;

public class VisualTransaction {

    private final UUID transactionUniqueId;
    private final UUID ownerUniqueId;
    private final String rankName;
    private final Instant buyTime;

    public VisualTransaction(UUID ownerUniqueId, String rankName) {
        this.transactionUniqueId = UUID.randomUUID();
        this.ownerUniqueId = ownerUniqueId;
        this.rankName = rankName;
        this.buyTime = Instant.now();
    }

    public VisualTransaction(UUID transactionUniqueId, UUID ownerUniqueId, String rankName, Instant instant) {
        this.transactionUniqueId = transactionUniqueId;
        this.ownerUniqueId = ownerUniqueId;
        this.rankName = rankName;
        this.buyTime = instant;
    }

    public UUID getTransactionUniqueId() {
        return this.transactionUniqueId;
    }

    public UUID getOwnerUniqueId() {
        return this.ownerUniqueId;
    }

    public String getRankName() {
        return this.rankName;
    }

    public Instant getBuyTime() {
        return this.buyTime;
    }

}