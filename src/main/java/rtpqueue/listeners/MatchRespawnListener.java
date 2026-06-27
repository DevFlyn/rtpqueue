package rtpqueue.listeners;

import rtpqueue.RTPQueue;
import rtpqueue.utils.RTPUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MatchRespawnListener implements Listener {

    private final RTPQueue plugin;
    private final Map<UUID, Location> pendingRespawns = new ConcurrentHashMap<>();

    public MatchRespawnListener(RTPQueue plugin) {
        this.plugin = plugin;
    }

    public void queueRespawn(Player player) {
        Location spawn = RTPUtil.getSpawnLocation(plugin);
        if (spawn != null) {
            pendingRespawns.put(player.getUniqueId(), spawn);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Location spawn = pendingRespawns.remove(player.getUniqueId());
        if (spawn != null) {
            event.setRespawnLocation(spawn);
        }
    }
}