package rtpqueue.elo;

import org.bukkit.ChatColor;

public enum EloRank {

    COPPER_I("Copper I", ChatColor.DARK_RED, 0),
    COPPER_II("Copper II", ChatColor.DARK_RED, 100),
    COPPER_III("Copper III", ChatColor.DARK_RED, 200),
    IRON_I("Iron I", ChatColor.GRAY, 300),
    IRON_II("Iron II", ChatColor.GRAY, 400),
    IRON_III("Iron III", ChatColor.GRAY, 500),
    GOLD_I("Gold I", ChatColor.GOLD, 600),
    GOLD_II("Gold II", ChatColor.GOLD, 700),
    GOLD_III("Gold III", ChatColor.GOLD, 800),
    DIAMOND_I("Diamond I", ChatColor.AQUA, 900),
    DIAMOND_II("Diamond II", ChatColor.AQUA, 1000),
    DIAMOND_III("Diamond III", ChatColor.AQUA, 1100),
    NETHERITE_I("Netherite I", ChatColor.DARK_PURPLE, 1200),
    NETHERITE_II("Netherite II", ChatColor.DARK_PURPLE, 1350),
    NETHERITE_III("Netherite III", ChatColor.DARK_PURPLE, 1500),
    CHAMPION("Champion", ChatColor.LIGHT_PURPLE, Integer.MAX_VALUE);

    private final String displayName;
    private final ChatColor color;
    private final int minElo;

    EloRank(String displayName, ChatColor color, int minElo) {
        this.displayName = displayName;
        this.color = color;
        this.minElo = minElo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public int getMinElo() {
        return minElo;
    }

    public String getColored() {
        return color + displayName;
    }

    public static EloRank fromElo(int elo) {
        EloRank result = COPPER_I;
        for (EloRank rank : values()) {
            if (rank == CHAMPION) continue;
            if (elo >= rank.minElo) {
                result = rank;
            }
        }
        return result;
    }

    public EloRank next() {
        EloRank[] values = values();
        int idx = ordinal() + 1;
        if (idx >= values.length) return this;
        if (values[idx] == CHAMPION) return this;
        return values[idx];
    }

    public int getNextElo() {
        EloRank n = next();
        if (n == this) return -1;
        return n.minElo;
    }
}