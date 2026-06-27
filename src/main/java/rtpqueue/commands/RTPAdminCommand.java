package rtpqueue.commands;

import rtpqueue.RTPQueue;
import rtpqueue.elo.EloManager;
import rtpqueue.elo.EloRank;
import rtpqueue.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RTPAdminCommand implements CommandExecutor {

    private final RTPQueue plugin;

    public RTPAdminCommand(RTPQueue plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("rtpqueue.admin")) {
            sender.sendMessage(ChatUtil.color("&cNo permission."));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "setelo" -> {
                if (args.length < 3) { sender.sendMessage(ChatUtil.color("&cUsage: /rtpadmin setelo <player> <amount>")); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { sender.sendMessage(ChatUtil.color("&cPlayer not found.")); return true; }
                int elo;
                try { elo = Integer.parseInt(args[2]); } catch (NumberFormatException e) { sender.sendMessage(ChatUtil.color("&cInvalid number.")); return true; }
                plugin.getEloManager().setElo(target.getUniqueId(), elo);
                sender.sendMessage(ChatUtil.color("&aSet " + target.getName() + "'s ELO to " + elo + "."));
            }
            case "addelo" -> {
                if (args.length < 3) { sender.sendMessage(ChatUtil.color("&cUsage: /rtpadmin addelo <player> <amount>")); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { sender.sendMessage(ChatUtil.color("&cPlayer not found.")); return true; }
                int amount;
                try { amount = Integer.parseInt(args[2]); } catch (NumberFormatException e) { sender.sendMessage(ChatUtil.color("&cInvalid number.")); return true; }
                plugin.getEloManager().addElo(target.getUniqueId(), amount);
                sender.sendMessage(ChatUtil.color("&aAdded " + amount + " ELO to " + target.getName() + "."));
            }
            case "removeelo" -> {
                if (args.length < 3) { sender.sendMessage(ChatUtil.color("&cUsage: /rtpadmin removeelo <player> <amount>")); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { sender.sendMessage(ChatUtil.color("&cPlayer not found.")); return true; }
                int amount;
                try { amount = Integer.parseInt(args[2]); } catch (NumberFormatException e) { sender.sendMessage(ChatUtil.color("&cInvalid number.")); return true; }
                plugin.getEloManager().removeElo(target.getUniqueId(), amount);
                sender.sendMessage(ChatUtil.color("&aRemoved " + amount + " ELO from " + target.getName() + "."));
            }
            case "leaderboard", "lb" -> {
                EloManager em = plugin.getEloManager();
                List<Map.Entry<UUID, Integer>> board = em.getLeaderboard();
                sender.sendMessage(ChatUtil.color("&5&lRanked Leaderboard:"));
                int limit = Math.min(10, board.size());
                for (int i = 0; i < limit; i++) {
                    Map.Entry<UUID, Integer> entry = board.get(i);
                    String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                    EloRank rank = EloRank.fromElo(entry.getValue());
                    boolean champ = i == 0;
                    String rankDisplay = champ ? EloRank.CHAMPION.getColored() : rank.getColored();
                    sender.sendMessage(ChatUtil.color("&7#" + (i + 1) + " &f" + name + " &7- " + rankDisplay + " &7(" + entry.getValue() + " ELO)"));
                }
            }
            case "reload" -> {
                plugin.reloadConfig();
                sender.sendMessage(ChatUtil.color("&aConfig reloaded."));
            }
            default -> sendHelp(sender);
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatUtil.color("&5&lRTPAdmin Commands:"));
        sender.sendMessage(ChatUtil.color("&7/rtpadmin setelo <player> <amount>"));
        sender.sendMessage(ChatUtil.color("&7/rtpadmin addelo <player> <amount>"));
        sender.sendMessage(ChatUtil.color("&7/rtpadmin removeelo <player> <amount>"));
        sender.sendMessage(ChatUtil.color("&7/rtpadmin leaderboard"));
        sender.sendMessage(ChatUtil.color("&7/rtpadmin reload"));
    }
}