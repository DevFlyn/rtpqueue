package rtpqueue.elo;

import rtpqueue.RTPQueue;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EloManager {

    private final RTPQueue plugin;
    private final File dataFile;
    private FileConfiguration data;
    private final Map<UUID, Integer> eloCache = new HashMap<>();

    public EloManager(RTPQueue plugin) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "elo.yml");
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        loadAll();
    }

    private void loadAll() {
        if (data.getConfigurationSection("elo") == null) return;
        for (String key : data.getConfigurationSection("elo").getKeys(false)) {
            eloCache.put(UUID.fromString(key), data.getInt("elo." + key));
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, Integer> entry : eloCache.entrySet()) {
            data.set("elo." + entry.getKey().toString(), entry.getValue());
        }
        try { data.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public int getElo(UUID uuid) {
        return eloCache.getOrDefault(uuid, plugin.getConfig().getInt("elo.starting-elo", 400));
    }

    public void setElo(UUID uuid, int elo) {
        int value = Math.max(0, elo);
        eloCache.put(uuid, value);
        data.set("elo." + uuid.toString(), value);
        try { data.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public void addElo(UUID uuid, int amount) {
        setElo(uuid, getElo(uuid) + amount);
    }

    public void removeElo(UUID uuid, int amount) {
        setElo(uuid, Math.max(0, getElo(uuid) - amount));
    }

    public EloRank getRank(UUID uuid) {
        return EloRank.fromElo(getElo(uuid));
    }

    public List<Map.Entry<UUID, Integer>> getLeaderboard() {
        List<Map.Entry<UUID, Integer>> list = new ArrayList<>(eloCache.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());
        return list;
    }

    public int getLeaderboardPosition(UUID uuid) {
        List<Map.Entry<UUID, Integer>> board = getLeaderboard();
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).getKey().equals(uuid)) return i + 1;
        }
        return -1;
    }

    public boolean isChampion(UUID uuid) {
        List<Map.Entry<UUID, Integer>> board = getLeaderboard();
        if (board.isEmpty()) return false;
        return board.get(0).getKey().equals(uuid);
    }
}