package com.ts.visualranks.visualrank.user;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface VisualUserRepository {

    CompletableFuture<Void> update(VisualUser user);

    CompletableFuture<VisualUser> findUser(UUID uniqueId);

}