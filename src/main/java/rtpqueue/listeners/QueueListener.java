package rtpqueue.listeners;

import rtpqueue.RTPQueue;
import rtpqueue.queue.Match;
import rtpqueue.queue.QueueManager;
import rtpqueue.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QueueListener implements Listener {

    private final RTPQueue plugin;

    public QueueListener(RTPQueue plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        QueueManager qm = plugin.getQueueManager();
        if (!qm.isInMatch(player.getUniqueId())) return;

        Match match = qm.getMatch(player.getUniqueId());
        if (match != null && match.isInCountdown()) {
            event.setCancelled(true);
            player.sendMessage(ChatUtil.color("&cYou cannot place blocks during the countdown!"));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        QueueManager qm = plugin.getQueueManager();
        if (!qm.isInMatch(player.getUniqueId())) return;

        Match match = qm.getMatch(player.getUniqueId());
        if (match != null && match.isInCountdown()) {
            event.setCancelled(true);
            player.sendMessage(ChatUtil.color("&cYou cannot break blocks during the countdown!"));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        QueueManager qm = plugin.getQueueManager();

        if (qm.isInQueue(player.getUniqueId())) {
            qm.leaveQueue(player);
        }

        if (qm.isInMatch(player.getUniqueId())) {
            Match match = qm.getMatch(player.getUniqueId());
            if (match != null) {
                Player opponent = match.getPlayer1().equals(player) ? match.getPlayer2() : match.getPlayer1();
                if (opponent != null && opponent.isOnline()) {
                    qm.endMatch(match, opponent, player);
                } else {
                    match.forceEnd();
                }
            }
        }
    }
}