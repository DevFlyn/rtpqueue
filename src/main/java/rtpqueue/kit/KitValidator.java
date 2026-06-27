package rtpqueue.kit;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class KitValidator {

    public static List<String> validate(ItemStack[] armor, ItemStack[] contents, ItemStack offhand) {
        List<String> errors = new ArrayList<>();

        int blastProtCount = 0;
        for (ItemStack piece : armor) {
            if (piece == null) continue;
            if (piece.getEnchantments().containsKey(Enchantment.BLAST_PROTECTION)) {
                blastProtCount++;
            }
        }
        if (blastProtCount > 1) {
            errors.add("&cMax 1 Blast Protection piece allowed.");
        }

        int totemCount = 0;
        int fireworkCount = 0;

        for (ItemStack item : contents) {
            if (item == null) continue;
            if (item.getType() == Material.MACE) {
                errors.add("&cMace is not allowed.");
            }
            if (item.getType() == Material.TRIDENT) {
                errors.add("&cSpear (Trident) is not allowed.");
            }
            if (item.getType() == Material.TOTEM_OF_UNDYING) {
                totemCount += item.getAmount();
            }
            if (item.getType() == Material.FIREWORK_ROCKET) {
                fireworkCount += item.getAmount();
            }
        }

        if (offhand != null) {
            if (offhand.getType() == Material.TOTEM_OF_UNDYING) {
                totemCount += offhand.getAmount();
            }
            if (offhand.getType() == Material.MACE) {
                errors.add("&cMace is not allowed.");
            }
        }

        if (totemCount > 8) {
            errors.add("&cMax 8 Totems of Undying allowed (you have " + totemCount + ").");
        }
        if (fireworkCount > 64) {
            errors.add("&cMax 64 Fireworks allowed (you have " + fireworkCount + ").");
        }

        return errors;
    }

    public static List<String> validateInventory(PlayerInventory inv) {
        ItemStack[] armor = inv.getArmorContents();
        ItemStack[] contents = inv.getStorageContents();
        ItemStack offhand = inv.getItemInOffHand();
        return validate(armor, contents, offhand);
    }
}