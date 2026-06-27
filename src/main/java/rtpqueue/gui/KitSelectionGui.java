package rtpqueue.gui;

import rtpqueue.RTPQueue;
import rtpqueue.kit.Kit;
import rtpqueue.kit.KitManager;
import rtpqueue.utils.ChatUtil;
import rtpqueue.utils.GuiUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitSelectionGui {

    private static final String TITLE = ChatUtil.color("&5&lRanked Kit Selection");

    public static class Holder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static int getStartSlot(int slots) {
        return 9 + (9 - slots) / 2;
    }

    public static void open(Player player, RTPQueue plugin) {
        int slots = plugin.getConfig().getInt("kit-slots", 5);
        if (slots > 9) slots = 9;

        Inventory inv = Bukkit.createInventory(new Holder(), 27, TITLE);

        KitManager km = plugin.getKitManager();
        List<Kit> kits = km.getKits(player.getUniqueId());
        int selected = km.getSelectedSlot(player.getUniqueId());

        int startSlot = getStartSlot(slots);

        for (int i = 0; i < slots; i++) {
            Kit kit = (i < kits.size()) ? kits.get(i) : null;
            String kitName = (kit != null) ? kit.getName() : "Kit " + (i + 1);

            ItemStack item;
            if (i == selected && kit != null) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatUtil.color("&a&lSELECTED"));
                lore.add(ChatUtil.color("&7Left-click: &fEdit kit"));
                lore.add(ChatUtil.color("&7Right-click: &fDeselect"));
                item = GuiUtil.makeGlow(Material.WRITABLE_BOOK, "&d" + kitName, lore.toArray(new String[0]));
            } else if (kit != null) {
                item = GuiUtil.makeItem(Material.WRITABLE_BOOK, "&7" + kitName,
                        "&7Left-click: &fSelect & edit",
                        "&7Right-click: &fEdit only"
                );
            } else {
                item = GuiUtil.makeItem(Material.BOOK, "&8" + kitName + " &7(Empty)",
                        "&7Click to create this kit."
                );
            }

            inv.setItem(startSlot + i, item);
        }

        player.openInventory(inv);
    }

    public static boolean isKitSelection(Inventory inv) {
        return inv != null && inv.getHolder() instanceof Holder;
    }

    public static String getTitle() { return TITLE; }
}