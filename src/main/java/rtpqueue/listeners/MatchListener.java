package rtpqueue.listeners;

import rtpqueue.RTPQueue;
import rtpqueue.queue.Match;
import rtpqueue.queue.QueueManager;
import rtpqueue.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MatchListener implements Listener {

    private final RTPQueue plugin;
    private final Map<UUID, Boolean> wasInMatch = new HashMap<>();

    public MatchListener(RTPQueue plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        QueueManager qm = plugin.getQueueManager();

        if (!qm.isInMatch(dead.getUniqueId())) return;

        Match match = qm.getMatch(dead.getUniqueId());
        if (match == null || !match.isFighting()) return;

        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.getDrops().clear();
        event.setDeathMessage(null);

        wasInMatch.put(dead.getUniqueId(), true);
        match.handleDeath(dead);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (wasInMatch.remove(uuid) == null) return;

        event.setRespawnLocation(player.getWorld().getSpawnLocation());

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            player.getInventory().clear();
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(20f);
        }, 1L);
    }
}