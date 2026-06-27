package rtpqueue.queue;

import rtpqueue.RTPQueue;
import rtpqueue.elo.EloCalculator;
import rtpqueue.elo.EloResultMessage;
import rtpqueue.kit.Kit;
import rtpqueue.kit.KitManager;
import rtpqueue.utils.ChatUtil;
import rtpqueue.utils.RTPUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class QueueManager {

    private final RTPQueue plugin;
    private final Queue<UUID> queue = new LinkedList<>();
    private final Map<UUID, Match> activeMatches = new HashMap<>();
    private final Map<UUID, UUID> playerToMatch = new HashMap<>();
    private final Map<UUID, UUID> lastOpponent = new HashMap<>();

    public QueueManager(RTPQueue plugin) {
        this.plugin = plugin;
        startMatchmaker();
    }

    private void startMatchmaker() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::tryMatch, 20L, 20L);
    }

    public boolean isInQueue(UUID uuid) {
        return queue.contains(uuid);
    }

    public boolean isInMatch(UUID uuid) {
        return playerToMatch.containsKey(uuid);
    }

    public Match getMatch(UUID uuid) {
        UUID matchId = playerToMatch.get(uuid);
        if (matchId == null) return null;
        return activeMatches.get(matchId);
    }

    public void joinQueue(Player player) {
        if (isInQueue(player.getUniqueId())) {
            player.sendMessage(ChatUtil.color("&cYou are already in queue."));
            return;
        }
        if (isInMatch(player.getUniqueId())) {
            player.sendMessage(ChatUtil.color("&cYou are already in a match."));
            return;
        }
        queue.add(player.getUniqueId());
        player.sendMessage(ChatUtil.color("&aYou joined the ranked queue. Players in queue: &f" + queue.size()));
    }

    public void leaveQueue(Player player) {
        if (!queue.remove(player.getUniqueId())) {
            player.sendMessage(ChatUtil.color("&cYou are not in queue."));
            return;
        }
        player.sendMessage(ChatUtil.color("&eYou left the ranked queue."));
    }

    private void tryMatch() {
        if (queue.size() < 2) return;

        UUID uuid1 = queue.poll();
        Player p1 = Bukkit.getPlayer(uuid1);

        if (p1 == null || !p1.isOnline()) {
            return;
        }

        UUID uuid2 = pickOpponent(uuid1);

        if (uuid2 == null) {
            queue.offer(uuid1);
            return;
        }

        Player p2 = Bukkit.getPlayer(uuid2);

        if (p2 == null || !p2.isOnline()) {
            queue.offer(uuid1);
            return;
        }

        startMatch(p1, p2);
    }

    private UUID pickOpponent(UUID uuid1) {
        UUID lastFaced = lastOpponent.get(uuid1);

        List<UUID> candidates = new ArrayList<>(queue);
        queue.clear();

        UUID chosen = null;
        for (UUID candidate : candidates) {
            if (chosen == null) {
                chosen = candidate;
                continue;
            }
            queue.offer(candidate);
        }

        if (chosen != null && chosen.equals(lastFaced) && candidates.size() > 1) {
            UUID fallback = chosen;
            chosen = queue.poll();
            queue.offer(fallback);
        }

        return chosen;
    }

    private void startMatch(Player p1, Player p2) {
        String worldName = plugin.getConfig().getString("world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            p1.sendMessage(ChatUtil.color("&cMatch world not found. Contact an admin."));
            p2.sendMessage(ChatUtil.color("&cMatch world not found. Contact an admin."));
            return;
        }

        Location loc1 = RTPUtil.findSafeLocation(world, plugin);
        Location loc2 = RTPUtil.findNearbyLocation(loc1, plugin);

        if (loc1 == null || loc2 == null) {
            p1.sendMessage(ChatUtil.color("&cCould not find safe locations. Requeuing..."));
            p2.sendMessage(ChatUtil.color("&cCould not find safe locations. Requeuing..."));
            queue.offer(p1.getUniqueId());
            queue.offer(p2.getUniqueId());
            return;
        }

        Match match = new Match(plugin, p1, p2, loc1, loc2);
        activeMatches.put(match.getId(), match);
        playerToMatch.put(p1.getUniqueId(), match.getId());
        playerToMatch.put(p2.getUniqueId(), match.getId());

        lastOpponent.put(p1.getUniqueId(), p2.getUniqueId());
        lastOpponent.put(p2.getUniqueId(), p1.getUniqueId());

        match.start();
    }

    public void endMatch(Match match, Player winner, Player loser) {
        activeMatches.remove(match.getId());
        playerToMatch.remove(match.getPlayer1().getUniqueId());
        playerToMatch.remove(match.getPlayer2().getUniqueId());

        int winnerElo = plugin.getEloManager().getElo(winner.getUniqueId());
        int loserElo = plugin.getEloManager().getElo(loser.getUniqueId());

        int gain = EloCalculator.calculateChange(winnerElo, loserElo, true);
        int loss = EloCalculator.calculateChange(loserElo, winnerElo, false);

        plugin.getEloManager().addElo(winner.getUniqueId(), gain);
        plugin.getEloManager().removeElo(loser.getUniqueId(), -loss);

        EloResultMessage.send(winner, plugin.getEloManager(), true, gain);
        EloResultMessage.send(loser, plugin.getEloManager(), false, loss);
    }

    public void shutdown() {
        for (Match match : new ArrayList<>(activeMatches.values())) {
            match.forceEnd();
        }
        activeMatches.clear();
        playerToMatch.clear();
        queue.clear();
    }

    public Map<UUID, Match> getActiveMatches() {
        return activeMatches;
    }
}