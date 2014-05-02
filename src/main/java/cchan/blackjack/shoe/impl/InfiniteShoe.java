package cchan.blackjack.shoe.impl;

import cchan.blackjack.model.Card;
import cchan.blackjack.shoe.Shoe;
import cchan.blackjack.strategy.Strategy;

/**
 * Simplest deck - it has an infinite number of cards. The probability of a particular
 * card being dealt from this deck remains constant throughout. Card-counting strategies
 * will not work on this type of deck. 
 */
public class InfiniteShoe implements Shoe {
    
    private Card nextCard = null;

    @Override
    public Card peek() {
        if (nextCard == null) {
            double rand = Math.random() * 13.0f; // will be [0.0, 13.0)
            int val = (int) Math.floor(rand);
            this.nextCard = Card.values()[val];
        }
        return this.nextCard;
    }

    @Override
    public Card getNextCard(boolean faceUp) {
        if (nextCard != null) {
            Card card = this.nextCard;
            this.nextCard = null;
            return card;
        }
        double rand = Math.random() * 13.0f; // will be [0.0, 13.0)
        int val = (int) Math.floor(rand);
        return Card.values()[val];
    }

    @Override
    public int getNumCardsRemaining() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void shuffle() {
        return;
    }

    @Override
    public void registerStrategy(Strategy strategy) {
        return;
    }

}
