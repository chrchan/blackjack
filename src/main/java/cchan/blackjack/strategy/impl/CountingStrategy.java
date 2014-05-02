package cchan.blackjack.strategy.impl;

import cchan.blackjack.model.Card;
import cchan.blackjack.shoe.Shoe;

/**
 * This strategy needs to observe the Shoe.getNextCard() events.
 *
 * @author Cchan2
 *
 */
public class CountingStrategy extends BasicStrategy {

    private int runningCount = 0;

    @Override
    public int getNextBet(Shoe shoe, int initialBet, int lastBet) {
        // convert running count to true count
        int trueCount = (int) (((double) runningCount) / (shoe.getNumCardsRemaining() / 52.0));
        System.out.println(String.format("runnningCount: %d, cardsRemaining: %d, trueCount: %d",
        		runningCount, shoe.getNumCardsRemaining(), trueCount));
        if (trueCount == 2 || trueCount == 3) {
            return initialBet * 2;
        } else if (trueCount == 4 || trueCount == 5) {
            return initialBet * 3;
        } else if (trueCount == 6 || trueCount == 7) {
            return initialBet * 4;
        } else if (trueCount > 7) {
            return initialBet * 5;
        }
        return initialBet;
    }

    @Override
    public void onDealCard(Card card) {
        int value = card.getValue();
        if (value >=2 && value <=6) {
            this.runningCount++;
        } else if (value == 10 || value == 1) {
            this.runningCount--;
        }
    }

    @Override
    public void onShuffle() {
        this.runningCount = 0;
    }
}
