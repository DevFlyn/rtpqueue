package rtpqueue.elo;

import rtpqueue.utils.ChatUtil;
import org.bukkit.entity.Player;

public class EloResultMessage {

    private static final int BAR_LENGTH = 10;

    public static void send(Player player, EloManager em, boolean won, int eloChange) {
        int oldElo = em.getElo(player.getUniqueId()) - eloChange;
        int newElo = em.getElo(player.getUniqueId());

        EloRank oldRank = EloRank.fromElo(oldElo);
        EloRank newRank = EloRank.fromElo(newElo);

        boolean isChampion = em.isChampion(player.getUniqueId());
        String oldRankName = oldRank.getDisplayName();
        String newRankName = isChampion ? EloRank.CHAMPION.getDisplayName() : newRank.getDisplayName();

        String resultWord = won ? "won" : "lost";
        String signedChange = (eloChange >= 0 ? "+" : "") + eloChange;

        player.sendMessage(ChatUtil.color("&5You have " + resultWord + " the match. (&7ELO Update: " + signedChange + " : " + newElo + "&5)"));
        player.sendMessage(ChatUtil.color("&fCurrent Rank (&7" + oldRankName + " -> " + newRankName + "&f)"));

        int percent = getProgressPercent(newRank, newElo, isChampion);
        player.sendMessage(ChatUtil.color("&fProgression bar (&7" + percent + "%&f)"));
        player.sendMessage(buildBar(percent));
    }

    private static int getProgressPercent(EloRank rank, int elo, boolean isChampion) {
        if (isChampion) return 100;

        int nextElo = rank.getNextElo();
        if (nextElo == -1) return 100;

        int span = nextElo - rank.getMinElo();
        if (span <= 0) return 100;

        int progress = elo - rank.getMinElo();
        int percent = (progress * 100) / span;
        return Math.max(0, Math.min(100, percent));
    }

    private static String buildBar(int percent) {
        int filled = (int) Math.round((percent / 100.0) * BAR_LENGTH);
        filled = Math.max(0, Math.min(BAR_LENGTH, filled));

        StringBuilder bar = new StringBuilder();
        bar.append("&a");
        for (int i = 0; i < filled; i++) bar.append("\u25CF");
        bar.append("&7");
        for (int i = filled; i < BAR_LENGTH; i++) bar.append("\u25CF");

        return ChatUtil.color(bar.toString());
    }
}