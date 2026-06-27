package rtpqueue.listeners;

import rtpqueue.RTPQueue;
import rtpqueue.queue.QueueManager;
import rtpqueue.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandBlockListener implements Listener {

    private final RTPQueue plugin;

    public CommandBlockListener(RTPQueue plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        QueueManager qm = plugin.getQueueManager();

        boolean restricted = qm.isInMatch(player.getUniqueId()) || qm.isInQueue(player.getUniqueId());
        if (!restricted) return;

        if (player.hasPermission("rtpqueue.admin")) return;

        String message = event.getMessage().toLowerCase();
        String command = message.split(" ")[0];

        List<String> whitelist = plugin.getConfig().getStringList("whitelist");
        for (String allowed : whitelist) {
            if (command.equalsIgnoreCase(allowed.toLowerCase())) return;
        }

        event.setCancelled(true);
        player.sendMessage(ChatUtil.color("&cYou cannot use that command while in queue or a match."));
    }
}