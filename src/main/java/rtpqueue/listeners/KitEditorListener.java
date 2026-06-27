package rtpqueue.listeners;

import rtpqueue.RTPQueue;
import rtpqueue.gui.KitEditorGui;
import rtpqueue.gui.KitSelectionGui;
import rtpqueue.kit.KitManager;
import rtpqueue.utils.ChatUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KitEditorListener implements Listener {

    private final RTPQueue plugin;

    public KitEditorListener(RTPQueue plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKitSelectionClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!KitSelectionGui.isKitSelection(event.getInventory())) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        int kitSlots = plugin.getConfig().getInt("kit-slots", 5);
        if (kitSlots > 9) kitSlots = 9;

        int startSlot = KitSelectionGui.getStartSlot(kitSlots);
        int rawSlot = event.getSlot();
        int slot = rawSlot - startSlot;

        if (slot < 0 || slot >= kitSlots) return;

        KitManager km = plugin.getKitManager();
        ClickType click = event.getClick();

        if (click == ClickType.LEFT) {
            km.setSelectedSlot(player.getUniqueId(), slot);
            player.closeInventory();
            KitEditorGui.open(player, plugin, slot);
        } else if (click == ClickType.RIGHT) {
            if (km.getKit(player.getUniqueId(), slot) != null) {
                player.closeInventory();
                KitEditorGui.open(player, plugin, slot);
            }
        }
    }

    @EventHandler
    public void onKitEditorClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory inv = event.getInventory();
        if (!KitEditorGui.isKitEditor(inv)) return;

        int rawSlot = event.getRawSlot();

        if (rawSlot == KitEditorGui.SLOT_IMPORT) {
            event.setCancelled(true);
            KitEditorGui.importInventory(player, inv);
            return;
        }

        if (rawSlot == KitEditorGui.SLOT_SAVE) {
            event.setCancelled(true);
            KitEditorGui.saveKit(player, inv, plugin);
            return;
        }

        if (rawSlot == KitEditorGui.SLOT_LEAVE) {
            event.setCancelled(true);
            KitEditorGui.clearEditingSlot(player.getUniqueId());
            player.closeInventory();
            KitSelectionGui.open(player, plugin);
            return;
        }

        if (KitEditorGui.isButtonSlot(rawSlot)) {
            event.setCancelled(true);
            return;
        }

        if (rawSlot >= 54) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onKitEditorDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Inventory inv = event.getInventory();
        if (!KitEditorGui.isKitEditor(inv)) return;

        for (int slot : event.getRawSlots()) {
            if (KitEditorGui.isButtonSlot(slot)) {
                event.setCancelled(true);
                return;
            }
            if (slot >= 54) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (KitEditorGui.isKitEditor(event.getInventory())) {
            KitEditorGui.clearEditingSlot(player.getUniqueId());
        }
    }
}