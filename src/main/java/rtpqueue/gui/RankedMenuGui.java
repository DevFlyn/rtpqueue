package rtpqueue.gui;

import rtpqueue.RTPQueue;
import rtpqueue.elo.EloManager;
import rtpqueue.elo.EloRank;
import rtpqueue.queue.QueueManager;
import rtpqueue.utils.ChatUtil;
import rtpqueue.utils.GuiUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class RankedMenuGui {

    private static final String TITLE = ChatUtil.color("&5Ranked Menu");

    public static class Holder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static void open(Player player, RTPQueue plugin) {
        Inventory inv = Bukkit.createInventory(new Holder(), 27, TITLE);

        EloManager em = plugin.getEloManager();
        QueueManager qm = plugin.getQueueManager();
        int elo = em.getElo(player.getUniqueId());
        EloRank rank = em.getRank(player.getUniqueId());
        boolean isChampion = em.isChampion(player.getUniqueId());
        String rankDisplay = isChampion ? EloRank.CHAMPION.getColored() : rank.getColored();

        int pos = em.getLeaderboardPosition(player.getUniqueId());
        String posStr = pos == -1 ? "N/A" : "#" + pos;

        String nextRankStr;
        if (isChampion || rank.getNextElo() == -1) {
            nextRankStr = "Max Rank";
        } else {
            nextRankStr = rank.getNextElo() + " Elo";
        }

        ItemStack statsItem = GuiUtil.makeItem(Material.PAPER, "&fStats",
                "&7Your Rank: " + rankDisplay,
                "&7Elo: &f" + elo,
                "&7Next Rank at: &f" + nextRankStr,
                "&7Leaderboard Pos: &f" + posStr
        );

        boolean inQueue = qm.isInQueue(player.getUniqueId());
        boolean inMatch = qm.isInMatch(player.getUniqueId());

        String queueLore;
        if (inMatch) queueLore = "&cYou are in a match.";
        else if (inQueue) queueLore = "&eClick to leave queue.";
        else queueLore = "&aClick to join ranked queue.";

        ItemStack queueItem = inQueue
                ? GuiUtil.makeGlow(Material.GREEN_DYE, "&cLeave Queue", queueLore)
                : GuiUtil.makeItem(Material.RED_DYE, "&aJoin Queue", queueLore);

        ItemStack kitItem = GuiUtil.makeItem(Material.WRITABLE_BOOK, "&dKits",
                "&7Click to open kit editor.",
                "&eRight-click &7to view match rules."
        );

        inv.setItem(10, kitItem);
        inv.setItem(13, queueItem);
        inv.setItem(16, statsItem);

        player.openInventory(inv);
    }

    public static boolean isRankedMenu(Inventory inv) {
        return inv != null && inv.getHolder() instanceof Holder;
    }
}