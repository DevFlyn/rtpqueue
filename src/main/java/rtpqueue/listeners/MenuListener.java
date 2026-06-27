package rtpqueue.listeners;

import rtpqueue.RTPQueue;
import rtpqueue.gui.KitSelectionGui;
import rtpqueue.gui.RankedMenuGui;
import rtpqueue.queue.QueueManager;
import rtpqueue.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.List;

public class MenuListener implements Listener {

    private final RTPQueue plugin;

    public MenuListener(RTPQueue plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!RankedMenuGui.isRankedMenu(event.getInventory())) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        int slot = event.getSlot();

        if (slot == 10) {
            if (event.getClick() == ClickType.RIGHT) {
                sendRules(player);
            } else {
                player.closeInventory();
                KitSelectionGui.open(player, plugin);
            }
            return;
        }

        if (slot == 13) {
            QueueManager qm = plugin.getQueueManager();
            if (qm.isInMatch(player.getUniqueId())) {
                player.sendMessage(ChatUtil.color("&cYou are already in a match."));
                return;
            }
            if (qm.isInQueue(player.getUniqueId())) {
                qm.leaveQueue(player);
            } else {
                qm.joinQueue(player);
            }
            player.closeInventory();
            return;
        }

        if (slot == 16) {
            player.closeInventory();
        }
    }

    private void sendRules(Player player) {
        player.closeInventory();
        List<String> rules = plugin.getConfig().getStringList("rules");
        for (String line : rules) {
            player.sendMessage(ChatUtil.color(line));
        }
    }
}