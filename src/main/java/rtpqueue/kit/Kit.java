package rtpqueue.kit;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Kit {

    private String name;
    private ItemStack[] armor;
    private ItemStack[] contents;
    private ItemStack offhand;

    public Kit(String name, ItemStack[] armor, ItemStack[] contents, ItemStack offhand) {
        this.name = name;
        this.armor = armor;
        this.contents = contents;
        this.offhand = offhand;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ItemStack[] getArmor() { return armor; }
    public void setArmor(ItemStack[] armor) { this.armor = armor; }

    public ItemStack[] getContents() { return contents; }
    public void setContents(ItemStack[] contents) { this.contents = contents; }

    public ItemStack getOffhand() { return offhand; }
    public void setOffhand(ItemStack offhand) { this.offhand = offhand; }
}