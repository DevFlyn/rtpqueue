package rtpqueue.kit;

import rtpqueue.RTPQueue;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.util.*;

public class KitManager {

    private final RTPQueue plugin;
    private final File dataFile;
    private FileConfiguration data;

    private final Map<UUID, List<Kit>> playerKits = new HashMap<>();
    private final Map<UUID, Integer> selectedKit = new HashMap<>();
    private final Map<UUID, Kit> lastKit = new HashMap<>();

    public KitManager(RTPQueue plugin) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "kits.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        loadAll();
    }

    private void loadAll() {
        if (data.getConfigurationSection("kits") == null) return;
        for (String uuidStr : data.getConfigurationSection("kits").getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            int slots = plugin.getConfig().getInt("kit-slots", 5);
            List<Kit> kits = new ArrayList<>();

            for (int i = 0; i < slots; i++) {
                String path = "kits." + uuidStr + ".slot" + i;
                if (!data.contains(path + ".name")) {
                    kits.add(null);
                    continue;
                }
                String name = data.getString(path + ".name", "Kit " + (i + 1));
                ItemStack[] armor = deserializeArray(data.getString(path + ".armor", ""), 4);
                ItemStack[] contents = deserializeArray(data.getString(path + ".contents", ""), 36);
                ItemStack offhand = deserializeSingle(data.getString(path + ".offhand", ""));
                kits.add(new Kit(name, armor, contents, offhand));
            }

            playerKits.put(uuid, kits);
            selectedKit.put(uuid, data.getInt("kits." + uuidStr + ".selected", 0));
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, List<Kit>> entry : playerKits.entrySet()) {
            String uuidStr = entry.getKey().toString();
            List<Kit> kits = entry.getValue();
            for (int i = 0; i < kits.size(); i++) {
                Kit kit = kits.get(i);
                if (kit == null) continue;
                String path = "kits." + uuidStr + ".slot" + i;
                data.set(path + ".name", kit.getName());
                data.set(path + ".armor", serializeArray(kit.getArmor()));
                data.set(path + ".contents", serializeArray(kit.getContents()));
                data.set(path + ".offhand", serializeSingle(kit.getOffhand()));
            }
            data.set("kits." + uuidStr + ".selected", selectedKit.getOrDefault(entry.getKey(), 0));
        }
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String serializeArray(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private ItemStack[] deserializeArray(String data, int size) {
        if (data == null || data.isEmpty()) return new ItemStack[size];
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            int length = dataInput.readInt();
            ItemStack[] items = new ItemStack[length];
            for (int i = 0; i < length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            dataInput.close();
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack[size];
        }
    }

    private String serializeSingle(ItemStack item) {
        if (item == null) return "";
        return serializeArray(new ItemStack[]{item});
    }

    private ItemStack deserializeSingle(String data) {
        if (data == null || data.isEmpty()) return null;
        ItemStack[] arr = deserializeArray(data, 1);
        return (arr.length > 0) ? arr[0] : null;
    }

    public List<Kit> getKits(UUID uuid) {
        int slots = plugin.getConfig().getInt("kit-slots", 5);
        return playerKits.computeIfAbsent(uuid, k -> {
            List<Kit> list = new ArrayList<>();
            for (int i = 0; i < slots; i++) list.add(null);
            return list;
        });
    }

    public Kit getKit(UUID uuid, int slot) {
        List<Kit> kits = getKits(uuid);
        if (slot < 0 || slot >= kits.size()) return null;
        return kits.get(slot);
    }

    public void saveKit(UUID uuid, int slot, Kit kit) {
        List<Kit> kits = getKits(uuid);
        while (kits.size() <= slot) kits.add(null);
        kits.set(slot, kit);
        saveAll();
    }

    public int getSelectedSlot(UUID uuid) {
        return selectedKit.getOrDefault(uuid, 0);
    }

    public void setSelectedSlot(UUID uuid, int slot) {
        selectedKit.put(uuid, slot);
        saveAll();
    }

    public Kit getSelectedKit(UUID uuid) {
        int slot = getSelectedSlot(uuid);
        Kit kit = getKit(uuid, slot);
        if (kit != null) return kit;
        for (Kit k : getKits(uuid)) {
            if (k != null) return k;
        }
        return lastKit.get(uuid);
    }

    public void setLastKit(UUID uuid, Kit kit) {
        lastKit.put(uuid, kit);
    }
}