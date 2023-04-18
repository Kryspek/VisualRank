package com.ts.visualranks.hook.implementation.placeholderapi;

import com.ts.visualranks.visualrank.VisualRankSetEvent;
import com.ts.visualranks.visualrank.user.VisualUser;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlaceholderApiController implements Listener {

    private final AsyncLoadingCache<UUID, VisualUser> visualUserCache;

    public PlaceholderApiController(AsyncLoadingCache<UUID, VisualUser> visualUserCache) {
        this.visualUserCache = visualUserCache;
    }

    @EventHandler
    void onVisualUserJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        this.visualUserCache.synchronous().refresh(uuid);
    }

    @EventHandler
    void onVisualUserQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        this.visualUserCache.synchronous().invalidate(uuid);
    }

    @EventHandler
    void onVisualRankUpdate(VisualRankSetEvent event) {
        UUID uuid = event.getUuid();

        this.visualUserCache.synchronous().refresh(uuid);
    }

}