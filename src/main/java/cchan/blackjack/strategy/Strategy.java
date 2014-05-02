package cchan.blackjack.strategy;

import cchan.blackjack.model.Action;
import cchan.blackjack.model.Card;
import cchan.blackjack.model.Hand;
import cchan.blackjack.shoe.Shoe;

public interface Strategy {

    public int getNextBet(Shoe deck, int initialBet, int lastBet);
    
    public Action getAction(Hand hand, Card dealerShows, Shoe deck);
    
    public void onDealCard(Card card);

    public void onShuffle();
}
