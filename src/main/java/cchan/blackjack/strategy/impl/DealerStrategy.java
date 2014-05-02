package cchan.blackjack.strategy.impl;

import cchan.blackjack.model.Action;
import cchan.blackjack.model.Card;
import cchan.blackjack.model.Hand;
import cchan.blackjack.shoe.Shoe;
import cchan.blackjack.strategy.Strategy;

public class DealerStrategy implements Strategy {

    @Override
    public Action getAction(Hand hand, Card dealerShows, Shoe deck) {
        // dealer always hits if total is under 17,
        // stays on 17 or higher,
        // and never splits, never doubles
        int total = hand.getBestTotal();
        if (total < 17) {
            return Action.HIT;
        }
        return Action.STAY;
    }

    @Override
    public int getNextBet(Shoe deck, int initialBet, int lastBet) {
        return 0;
    }

    @Override
    public void onDealCard(Card card) {
        return;
    }
    
    @Override
    public void onShuffle() {
        return;
    }
    
}
