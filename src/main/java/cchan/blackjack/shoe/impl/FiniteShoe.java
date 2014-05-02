package cchan.blackjack.shoe.impl;

import java.util.LinkedList;
import java.util.List;

import cchan.blackjack.model.Card;
import cchan.blackjack.shoe.Shoe;
import cchan.blackjack.strategy.Strategy;

public class FiniteShoe implements Shoe {
    
    private static final int RESHUFFLE_THRESHOLD = 30;
    
    private int nextIndex = 0;
    
    private int numCards = 0;
    
    private int numDecks = 0;
    
    private Card[] contents = null;
    
    // keep a list of Strategies that we have to notify when a card is dealt or shoe is reshuffled
    private List<Strategy> strategies = null;
    
    public FiniteShoe() {
        this(4);
    }
    
    public FiniteShoe(int numDecks) {
        this.numDecks = numDecks;
        this.numCards = this.numDecks * 52;
        this.contents = new Card[numCards];
        this.strategies = new LinkedList<Strategy>();
        this.shuffle();
    }

    @Override
    public Card peek() {
        return contents[nextIndex];
    }

    @Override
    public Card getNextCard(boolean faceUp) {
        Card card = contents[nextIndex];
        nextIndex--;
        // if card is being dealt face up, notify all registered Strategies
        if (faceUp) {
            for(Strategy strategy : strategies) {
                strategy.onDealCard(card);
            }
        }
        return card;
    }

    @Override
    public int getNumCardsRemaining() {
        return nextIndex+1;
    }

    @Override
    public void shuffle() {
        // reshuffle if less than 50 cards left
        if (this.getNumCardsRemaining() < RESHUFFLE_THRESHOLD) {
            
            System.out.println("** RESHUFFLE **");

            // first put the cards in
            this.nextIndex = 0;
            for(int i=0; i<numDecks; i++) {
                for(int suit=0; suit<4; suit++) {
                    for(Card card : Card.values()) {
                        contents[nextIndex] = card;
                        this.nextIndex++;
                    }
                }
            }
            
            // then randomize
            for(int i=0; i<nextIndex; i++) {
                int rand = (int) Math.floor((Math.random() * (nextIndex)));
                Card temp = contents[i];
                contents[i] = contents[rand];
                contents[rand] = temp;
            }
            this.nextIndex--; // because we started at zero

            // notify all registered listening Strategy objects
            for(Strategy strategy : strategies) {
                strategy.onShuffle();
            }
        
        }
    }

    @Override
    public void registerStrategy(Strategy strategy) {
        strategies.add(strategy);
    }

}
