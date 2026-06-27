package rtpqueue.utils;

import rtpqueue.RTPQueue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;

public class RTPUtil {

    private static final Random RANDOM = new Random();

    public static Location findSafeLocation(World world, RTPQueue plugin) {
        int minDist = plugin.getConfig().getInt("rtp.min-distance", 50);
        int maxDist = plugin.getConfig().getInt("rtp.max-distance", 500);

        for (int attempt = 0; attempt < 50; attempt++) {
            int x = (RANDOM.nextBoolean() ? 1 : -1) * (minDist + RANDOM.nextInt(maxDist - minDist));
            int z = (RANDOM.nextBoolean() ? 1 : -1) * (minDist + RANDOM.nextInt(maxDist - minDist));
            int y = world.getHighestBlockYAt(x, z);

            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
            if (isSafe(loc)) return loc;
        }
        return null;
    }

    public static Location findNearbyLocation(Location base, RTPQueue plugin) {
        if (base == null) return null;
        int spread = 20;
        for (int attempt = 0; attempt < 30; attempt++) {
            int dx = (RANDOM.nextBoolean() ? 1 : -1) * (5 + RANDOM.nextInt(spread));
            int dz = (RANDOM.nextBoolean() ? 1 : -1) * (5 + RANDOM.nextInt(spread));
            int x = base.getBlockX() + dx;
            int z = base.getBlockZ() + dz;
            int y = base.getWorld().getHighestBlockYAt(x, z);

            Location loc = new Location(base.getWorld(), x + 0.5, y + 1, z + 0.5);
            if (isSafe(loc)) return loc;
        }
        return null;
    }

    private static boolean isSafe(Location loc) {
        Material below = loc.clone().subtract(0, 1, 0).getBlock().getType();
        Material at = loc.getBlock().getType();
        Material above = loc.clone().add(0, 1, 0).getBlock().getType();

        return below.isSolid()
                && below != Material.LAVA
                && at == Material.AIR
                && above == Material.AIR;
    }
    public static Location getSpawnLocation(RTPQueue plugin) {
        String worldName = plugin.getConfig().getString("spawn.world", "world");
        double x = plugin.getConfig().getDouble("spawn.x", 0.5);
        double y = plugin.getConfig().getDouble("spawn.y", 64);
        double z = plugin.getConfig().getDouble("spawn.z", 0.5);

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        return new Location(world, x, y, z);
    }
}