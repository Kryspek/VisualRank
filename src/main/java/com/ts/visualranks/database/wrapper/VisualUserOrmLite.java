package com.ts.visualranks.database.wrapper;

import com.ts.visualranks.database.DatabaseManager;
import com.ts.visualranks.scheduler.Scheduler;
import com.ts.visualranks.visualrank.transaction.VisualTransaction;
import com.ts.visualranks.visualrank.user.VisualUser;
import com.ts.visualranks.visualrank.user.VisualUserRepository;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import panda.std.Option;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VisualUserOrmLite extends AbstractRepositoryOrmLite implements VisualUserRepository {

    public VisualUserOrmLite(DatabaseManager databaseManager, Scheduler scheduler) {
        super(databaseManager, scheduler);
    }

    @Override
    public CompletableFuture<Void> update(VisualUser owner) {
        return CompletableFuture.supplyAsync(() -> {
            for (VisualTransaction transaction : owner.getTransactions()) {
                TransactionWrapper transactionWrapper = new TransactionWrapper(
                        transaction.getTransactionUniqueId(),
                        transaction.getOwnerUniqueId(),
                        transaction.getRankName(),
                        transaction.getBuyTime().toString()
                );

                this.awaitSave(TransactionWrapper.class, transactionWrapper);
            }

            VisualUserWrapper visualUserWrapper = new VisualUserWrapper(owner.getUniqueId(), owner.getCurrentRank());
            this.awaitSave(VisualUserWrapper.class, visualUserWrapper);

            return null;
        });
    }

    @Override
    public CompletableFuture<VisualUser> findUser(UUID owner) {
        return CompletableFuture.supplyAsync(() -> {
            Option<String> currentRank = this.awaitAction(VisualUserWrapper.class, dao -> Option.supplyThrowing(Throwable.class, () -> dao.queryBuilder()
                    .where()
                    .eq("uuid", owner)
                    .queryForFirst()
            )).map(VisualUserWrapper::toVisualUser).map(VisualUser::getCurrentRank);

            List<VisualTransaction> ownerTransactions = this.awaitAction(TransactionWrapper.class, dao -> Option.supplyThrowing(Throwable.class, () -> dao.queryBuilder()
                    .where()
                    .eq("owner_uuid", owner)
                    .query()
            ))
                    .map(wrappers -> wrappers.stream().map(TransactionWrapper::toVisualTransaction).toList())
                    .orElseGet(new ArrayList<>());

            VisualUser visualUser = new VisualUser(owner, ownerTransactions);

            if (currentRank.isPresent()) {
                visualUser.setCurrentRank(currentRank.get());

                return visualUser;
            }

            return visualUser;
        });
    }

    @DatabaseTable(tableName = "bought_ranks")
    private static class TransactionWrapper {
        @DatabaseField(columnName = "uuid", id = true)
        private UUID boughtRankId;

        @DatabaseField(columnName = "owner_uuid")
        private UUID ownerUuid;

        @DatabaseField(columnName = "rank_name")
        private String rank;

        @DatabaseField(columnName = "buy_time")
        private String buyTime;

        public TransactionWrapper() { }

        public TransactionWrapper(UUID boughtRankId, UUID ownerUuid, String rank, String buyTime) {
            this.boughtRankId = boughtRankId;
            this.ownerUuid = ownerUuid;
            this.rank = rank;
            this.buyTime = buyTime;
        }

        public VisualTransaction toVisualTransaction() {
            return new VisualTransaction(this.boughtRankId, this.ownerUuid, this.rank, Instant.parse(this.buyTime));
        }
    }

    @DatabaseTable(tableName = "users")
    private static class VisualUserWrapper {
        @DatabaseField(columnName = "uuid", id = true)
        private UUID uuid;

        @DatabaseField(columnName = "current_rank_name")
        private String currentRankName;

        public VisualUserWrapper() { }

        public VisualUserWrapper(UUID uuid, String currentRankName) {
            this.uuid = uuid;
            this.currentRankName = currentRankName;
        }

        public VisualUser toVisualUser() {
            VisualUser visualUser = new VisualUser(this.uuid, new ArrayList<>());
            visualUser.setCurrentRank(this.currentRankName);

            return visualUser;
        }
    }

    public static VisualUserRepository create(DatabaseManager databaseManager, Scheduler scheduler) {
        try {
            TableUtils.createTableIfNotExists(databaseManager.connectionSource(), TransactionWrapper.class);
            TableUtils.createTableIfNotExists(databaseManager.connectionSource(), VisualUserWrapper.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new VisualUserOrmLite(databaseManager, scheduler);
    }

}