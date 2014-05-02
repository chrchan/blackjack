package cchan.blackjack.shoe;

import cchan.blackjack.model.Card;
import cchan.blackjack.strategy.Strategy;

public interface Shoe {

    // Gets what the next card to be dealt will be without actually dealing it.
    public Card peek();
    
    // Deals a card from the deck. Returns the value of the card.
    // All face cards will be represented as a 10. An Ace will be represented as a 1.
    public Card getNextCard(boolean faceUp);
    
    // Gets the number of undealt cards still in the deck. Used to determine if the deck needs to be reshuffled.
    public int getNumCardsRemaining();
    
    // Shuffles the deck if it needs to be.
    public void shuffle();
    
    public void registerStrategy(Strategy strategy);
    
}
