public class DualCard {

    private final Card alpha;
    private final Card beta;

    public DualCard(Card c1, Card c2) {
        alpha = c1;
        beta = c2;
    }

    public Card getAlpha() {
        return alpha;
    }

    public Card getBeta() {
        return beta;
    }

}
