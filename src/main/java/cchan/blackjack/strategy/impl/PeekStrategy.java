package cchan.blackjack.strategy.impl;

import cchan.blackjack.model.Action;
import cchan.blackjack.model.Card;
import cchan.blackjack.model.Hand;
import cchan.blackjack.shoe.Shoe;
import cchan.blackjack.strategy.Strategy;

/**
 * Cheat strategy that makes decisions on actions by peeking at the next
 * card to be dealt. This strategy does not adjust bet amounts. 
 */
public class PeekStrategy implements Strategy {

    @Override
    public Action getAction(Hand hand, Card dealerShows, Shoe deck) {
        Card nextCard = deck.peek();
        int totalSoFar = hand.getBestTotal();
        
        // if the next card gives us 21, double-down
        if (hand.isDoubleable() && totalSoFar + nextCard.getValue() == 21) {
            return Action.DOUBLE;
        }
        
        // if the next card keeps us at 21 or under, hit
        if (totalSoFar + nextCard.getValue() <= 21) {
            return Action.HIT;
        }
        
        return Action.STAY;
    }

    @Override
    public int getNextBet(Shoe deck, int initialBet, int lastBet) {
        return initialBet; // this strategy always bets the same amount
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
