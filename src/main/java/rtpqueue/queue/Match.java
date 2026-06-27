package rtpqueue.queue;

import rtpqueue.RTPQueue;
import rtpqueue.kit.Kit;
import rtpqueue.kit.KitManager;
import rtpqueue.utils.ChatUtil;
import rtpqueue.utils.RTPUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class Match {

    private final RTPQueue plugin;
    private final UUID id;
    private final Player player1;
    private final Player player2;
    private final Location loc1;
    private final Location loc2;

    private MatchState state = MatchState.COUNTDOWN;
    private BukkitTask countdownTask;
    private BukkitTask timeoutTask;

    public Match(RTPQueue plugin, Player player1, Player player2, Location loc1, Location loc2) {
        this.plugin = plugin;
        this.id = UUID.randomUUID();
        this.player1 = player1;
        this.player2 = player2;
        this.loc1 = loc1;
        this.loc2 = loc2;
    }

    public void start() {
        applyKit(player1);
        applyKit(player2);

        player1.teleport(loc1);
        player2.teleport(loc2);

        player1.sendMessage(ChatUtil.color("&6Match found! vs &f" + player2.getName()));
        player2.sendMessage(ChatUtil.color("&6Match found! vs &f" + player1.getName()));

        int countdownSeconds = plugin.getConfig().getInt("rtp.countdown-seconds", 3);
        startCountdown(countdownSeconds);
    }

    private void applyKit(Player player) {
        KitManager km = plugin.getKitManager();
        Kit kit = km.getSelectedKit(player.getUniqueId());
        if (kit == null) return;

        km.setLastKit(player.getUniqueId(), kit);
        player.getInventory().clear();
        player.getInventory().setArmorContents(kit.getArmor());
        player.getInventory().setStorageContents(kit.getContents());
        player.getInventory().setItemInOffHand(kit.getOffhand());
        player.updateInventory();
    }

    private void startCountdown(int seconds) {
        state = MatchState.COUNTDOWN;
        final int[] remaining = {seconds};

        player1.sendTitle(ChatUtil.color("&e" + seconds), ChatUtil.color("&7Get ready!"), 5, 25, 10);
        player2.sendTitle(ChatUtil.color("&e" + seconds), ChatUtil.color("&7Get ready!"), 5, 25, 10);

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            remaining[0]--;
            if (remaining[0] > 0) {
                player1.sendTitle(ChatUtil.color("&e" + remaining[0]), ChatUtil.color("&7Get ready!"), 5, 25, 10);
                player2.sendTitle(ChatUtil.color("&e" + remaining[0]), ChatUtil.color("&7Get ready!"), 5, 25, 10);
            } else {
                countdownTask.cancel();
                state = MatchState.FIGHTING;
                player1.sendTitle(ChatUtil.color("&aFIGHT!"), "", 5, 20, 10);
                player2.sendTitle(ChatUtil.color("&aFIGHT!"), "", 5, 20, 10);
                player1.sendMessage(ChatUtil.color("&aThe match has begun!"));
                player2.sendMessage(ChatUtil.color("&aThe match has begun!"));

                timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (state == MatchState.FIGHTING) {
                        player1.sendMessage(ChatUtil.color("&eMatch timed out. No winner."));
                        player2.sendMessage(ChatUtil.color("&eMatch timed out. No winner."));
                        forceEnd();
                    }
                }, 20L * 60 * 10);
            }
        }, 20L, 20L);
    }

    public boolean isInCountdown() {
        return state == MatchState.COUNTDOWN;
    }

    public boolean isFighting() {
        return state == MatchState.FIGHTING;
    }

    public void handleDeath(Player dead) {
        if (state != MatchState.FIGHTING) return;
        state = MatchState.ENDED;
        cancelTasks();

        Player winner = dead.equals(player1) ? player2 : player1;
        plugin.getQueueManager().endMatch(this, winner, dead);
        sendPlayersBack(winner, dead);
    }

    private void sendPlayersBack(Player winner, Player loser) {
        Location spawn = RTPUtil.getSpawnLocation(plugin);

        if (winner.isOnline()) {
            if (spawn != null) winner.teleport(spawn);
            winner.sendMessage(ChatUtil.color("&aTeleported back to spawn."));
        }
        if (loser.isOnline()) {
            plugin.getMatchRespawnListener().queueRespawn(loser);
        }
    }


    public void forceEnd() {
        state = MatchState.ENDED;
        cancelTasks();
    }

    private void cancelTasks() {
        if (countdownTask != null && !countdownTask.isCancelled()) countdownTask.cancel();
        if (timeoutTask != null && !timeoutTask.isCancelled()) timeoutTask.cancel();
    }

    public UUID getId() { return id; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public MatchState getState() { return state; }

    public enum MatchState {
        COUNTDOWN, FIGHTING, ENDED
    }
}