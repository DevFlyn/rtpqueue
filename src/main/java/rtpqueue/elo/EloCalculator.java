package rtpqueue.elo;

public class EloCalculator {

    private static final int BASE_CHANGE = 25;
    private static final double GAP_SCALE = 0.15;
    private static final int MIN_CHANGE = 5;
    private static final int MAX_CHANGE = 200;

    public static int calculateChange(int playerElo, int opponentElo, boolean won) {
        int gap = opponentElo - playerElo;

        double change;
        if (won) {
            change = BASE_CHANGE + GAP_SCALE * gap;
        } else {
            change = BASE_CHANGE - GAP_SCALE * gap;
        }

        change = Math.max(MIN_CHANGE, Math.min(MAX_CHANGE, change));

        return won ? (int) Math.round(change) : -(int) Math.round(change);
    }
}