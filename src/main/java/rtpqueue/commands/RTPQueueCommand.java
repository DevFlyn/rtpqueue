package rtpqueue.commands;

import rtpqueue.RTPQueue;
import rtpqueue.elo.EloManager;
import rtpqueue.elo.EloRank;
import rtpqueue.gui.RankedMenuGui;
import rtpqueue.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RTPQueueCommand implements CommandExecutor {

    private static final int LEADERBOARD_SIZE = 10;

    private final RTPQueue plugin;

    public RTPQueueCommand(RTPQueue plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("rtpqueue.play")) {
            player.sendMessage(ChatUtil.color("&cYou don't have permission to use ranked queue."));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("leaderboard")) {
            sendLeaderboard(player);
            return true;
        }

        RankedMenuGui.open(player, plugin);
        return true;
    }

    private void sendLeaderboard(Player player) {
        EloManager em = plugin.getEloManager();
        List<Map.Entry<UUID, Integer>> board = em.getLeaderboard();

        player.sendMessage(ChatUtil.color("&5&lTop " + LEADERBOARD_SIZE + " Ranked Leaderboard"));

        if (board.isEmpty()) {
            player.sendMessage(ChatUtil.color("&7No ranked players yet."));
            return;
        }

        int limit = Math.min(LEADERBOARD_SIZE, board.size());
        for (int i = 0; i < limit; i++) {
            Map.Entry<UUID, Integer> entry = board.get(i);
            UUID uuid = entry.getKey();
            int elo = entry.getValue();

            OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
            String name = offline.getName() != null ? offline.getName() : uuid.toString();

            boolean isChampion = (i == 0);
            String rankDisplay = isChampion ? EloRank.CHAMPION.getColored() : EloRank.fromElo(elo).getColored();

            player.sendMessage(ChatUtil.color("&7#" + (i + 1) + " &f" + name + " &7- " + rankDisplay + " &7(&f" + elo + " Elo&7)"));
        }
    }
}