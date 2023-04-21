package com.ts.visualranks.hook.implementation.placeholderapi;

import com.ts.visualranks.configuration.implementation.VisualRankConfiguration;
import com.ts.visualranks.configuration.implementation.VisualRankItem;
import com.ts.visualranks.hook.Hook;
import com.ts.visualranks.visualrank.VisualRank;
import com.ts.visualranks.visualrank.VisualRankManager;
import com.ts.visualranks.visualrank.user.VisualUser;
import com.ts.visualranks.visualrank.user.VisualUserRepository;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlaceholderApiHook extends PlaceholderExpansion implements Hook {

    private final AsyncLoadingCache<UUID, VisualUser> visualUserCache;
    private final VisualRankManager visualRankManager;
    private final LuckPerms luckPerms;

    public PlaceholderApiHook(VisualUserRepository transactionRepository, VisualRankManager visualRankManager, LuckPerms luckPerms) {
        this.visualUserCache = Caffeine.newBuilder()
                .refreshAfterWrite(3, TimeUnit.SECONDS)
                .buildAsync(key -> transactionRepository.findUser(key).get(15, TimeUnit.SECONDS));
        this.visualRankManager = visualRankManager;
        this.luckPerms = luckPerms;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("rank")) {
            UUID uuid = player.getUniqueId();
            VisualUser visualUser = this.visualUserCache.synchronous().getIfPresent(uuid);

            if (visualUser == null) {
                return this.getUserGroupPrefix(player);
            }

            if (visualUser.getCurrentRank().equalsIgnoreCase("")) {
                return this.getUserGroupPrefix(player);
            }

            Optional<VisualRankItem> visualRankOptional = this.visualRankManager.getVisualRank(visualUser.getCurrentRank());

            if (visualRankOptional.isEmpty()) {
                return this.getUserGroupPrefix(player);
            }

            VisualRank visualRank = visualRankOptional.get();

            return this.getUserGroupPrefixColor(player) + visualRank.getName();
        }

        return "Unknown placeholder";
    }

    private String getUserGroupPrefix(Player player) {
        CachedMetaData metaData = this.luckPerms.getPlayerAdapter(Player.class).getMetaData(player);

        return metaData.getPrefix();
    }

    private String getUserGroupPrefixColor(Player player) {
        String prefix = this.getUserGroupPrefix(player);

        if (prefix == null) {
            return "&7";
        }

        return prefix.substring(0, 2);
    }

    @Override
    public void initialize() {
        this.register();
    }

    @Override
    public String pluginName() {
        return "PlaceholderAPI";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "visualranks";
    }

    @Override
    public @NotNull String getAuthor() {
        return "eripe14";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    public AsyncLoadingCache<UUID, VisualUser> getVisualUserCache() {
        return this.visualUserCache;
    }

}