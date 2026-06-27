package rtpqueue.gui;

import rtpqueue.RTPQueue;
import rtpqueue.kit.Kit;
import rtpqueue.kit.KitManager;
import rtpqueue.kit.KitValidator;
import rtpqueue.utils.ChatUtil;
import rtpqueue.utils.GuiUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KitEditorGui {

    public static final String TITLE_PREFIX = ChatUtil.color("&5Kit Editor: ");

    public static final int SLOT_HELMET     = 36;
    public static final int SLOT_CHESTPLATE = 37;
    public static final int SLOT_LEGGINGS   = 38;
    public static final int SLOT_BOOTS      = 39;
    public static final int SLOT_OFFHAND    = 40;
    public static final int SLOT_IMPORT     = 51;
    public static final int SLOT_SAVE       = 52;
    public static final int SLOT_LEAVE      = 53;

    private static final Map<UUID, Integer> editingSlot = new HashMap<>();

    public static class Holder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static void open(Player player, RTPQueue plugin, int kitSlot) {
        KitManager km = plugin.getKitManager();
        Kit kit = km.getKit(player.getUniqueId(), kitSlot);

        String kitName = (kit != null) ? kit.getName() : "Kit " + (kitSlot + 1);
        Inventory inv = Bukkit.createInventory(new Holder(), 54, TITLE_PREFIX + kitName);

        if (kit != null) {
            ItemStack[] contents = kit.getContents();
            ItemStack[] armor = kit.getArmor();
            ItemStack offhand = kit.getOffhand();

            for (int i = 0; i < 36; i++) {
                if (contents[i] != null) inv.setItem(i, contents[i].clone());
            }
            if (armor[3] != null) inv.setItem(SLOT_HELMET,     armor[3].clone());
            if (armor[2] != null) inv.setItem(SLOT_CHESTPLATE, armor[2].clone());
            if (armor[1] != null) inv.setItem(SLOT_LEGGINGS,   armor[1].clone());
            if (armor[0] != null) inv.setItem(SLOT_BOOTS,      armor[0].clone());
            if (offhand  != null) inv.setItem(SLOT_OFFHAND,    offhand.clone());
        }

        placeButtons(inv);
        editingSlot.put(player.getUniqueId(), kitSlot);
        player.openInventory(inv);
    }

    private static void placeButtons(Inventory inv) {
        inv.setItem(SLOT_IMPORT, GuiUtil.makeItem(Material.CHEST,    "&cImport Inventory", "&7Imports your current inventory into the kit."));
        inv.setItem(SLOT_SAVE,   GuiUtil.makeItem(Material.BARRIER,  "&cSave Kit",         "&7Validates and saves your kit."));
        inv.setItem(SLOT_LEAVE,  GuiUtil.makeItem(Material.OAK_DOOR, "&cLeave Editor",     "&7Exit without saving."));

        for (int s : new int[]{41, 42, 43, 44, 45, 46, 47, 48,49, 50}) {
            inv.setItem(s, GuiUtil.filler());
        }
    }

    public static void importInventory(Player player, Inventory inv) {
        PlayerInventory pi = player.getInventory();

        for (int i = 0; i < 36; i++) {
            ItemStack item = pi.getItem(i);
            inv.setItem(i, item != null ? item.clone() : null);
        }

        ItemStack[] armor = pi.getArmorContents();
        if (armor[3] != null) inv.setItem(SLOT_HELMET,     armor[3].clone());
        if (armor[2] != null) inv.setItem(SLOT_CHESTPLATE, armor[2].clone());
        if (armor[1] != null) inv.setItem(SLOT_LEGGINGS,   armor[1].clone());
        if (armor[0] != null) inv.setItem(SLOT_BOOTS,      armor[0].clone());

        ItemStack offhand = pi.getItemInOffHand();
        if (offhand.getType() != Material.AIR) inv.setItem(SLOT_OFFHAND, offhand.clone());

        placeButtons(inv);
        player.sendMessage(ChatUtil.color("&aInventory imported! Click &c&lSave Kit &ato save."));
    }

    public static void saveKit(Player player, Inventory inv, RTPQueue plugin) {
        int slot = editingSlot.getOrDefault(player.getUniqueId(), 0);
        KitManager km = plugin.getKitManager();

        ItemStack[] contents = new ItemStack[36];
        for (int i = 0; i < 36; i++) contents[i] = inv.getItem(i);

        ItemStack[] armor = new ItemStack[4];
        armor[3] = inv.getItem(SLOT_HELMET);
        armor[2] = inv.getItem(SLOT_CHESTPLATE);
        armor[1] = inv.getItem(SLOT_LEGGINGS);
        armor[0] = inv.getItem(SLOT_BOOTS);
        ItemStack offhand = inv.getItem(SLOT_OFFHAND);

        List<String> errors = KitValidator.validate(armor, contents, offhand);
        if (!errors.isEmpty()) {
            player.sendMessage(ChatUtil.color("&cKit validation failed:"));
            for (String err : errors) player.sendMessage(ChatUtil.color(err));
            return;
        }

        String kitName = km.getKit(player.getUniqueId(), slot) != null
                ? km.getKit(player.getUniqueId(), slot).getName()
                : "Kit " + (slot + 1);

        km.saveKit(player.getUniqueId(), slot, new Kit(kitName, armor, contents, offhand));
        player.sendMessage(ChatUtil.color("&aKit &f" + kitName + " &asaved!"));
        clearEditingSlot(player.getUniqueId());
        player.closeInventory();
        KitSelectionGui.open(player, plugin);
    }

    public static boolean isButtonSlot(int slot) {
        return slot == SLOT_HELMET || slot == SLOT_CHESTPLATE || slot == SLOT_LEGGINGS
                || slot == SLOT_BOOTS  || slot == SLOT_OFFHAND
                || slot == SLOT_IMPORT || slot == SLOT_SAVE   || slot == SLOT_LEAVE
                || slot == 42 || slot == 43 || slot == 44
                || slot == 45 || slot == 46 || slot == 47
                || slot == 48 || slot == 50 || slot == 51 || slot == 52;
    }

    public static boolean isKitEditor(Inventory inv) {
        return inv != null && inv.getHolder() instanceof Holder;
    }

    public static int getEditingSlot(UUID uuid) {
        return editingSlot.getOrDefault(uuid, 0);
    }

    public static void clearEditingSlot(UUID uuid) {
        editingSlot.remove(uuid);
    }
}