package cchan.blackjack.model;

import java.util.LinkedList;
import java.util.List;

import cchan.blackjack.PlayerStats;
import cchan.blackjack.shoe.Shoe;

/**
 * Encapsulation of a players bet + cards. 
 */
public class Hand {

    private int bet = 0;
    
    private List<Card> cards = new LinkedList<Card>();
    
    private boolean doubleable = true;
    
    private boolean hasAces = false;
    
    private boolean hittable = true;
    
    private boolean splittable = true;
    
    private PlayerStats playerStats;
    
    private Hand() {
    }
    
    // create a dealer hand
    public static Hand createDealerHand(PlayerStats stats, Shoe shoe) {
        Hand hand = new Hand();
        hand.playerStats = stats;
        hand.bet = 0;
        hand.addCard(shoe.getNextCard(true)); // if it's a Dealer hand, only one card is dealt up
        hand.addCard(shoe.getNextCard(true));
        return hand;
    }
    
    // create a player hand
    public static Hand createPlayerHand(PlayerStats stats, Shoe shoe, Integer initialBet) {
        Hand hand = new Hand();
        hand.playerStats = stats;
        hand.bet = initialBet;
        hand.addCard(shoe.getNextCard(true)); // if it's a player hand, both cards are dealt face-up
        hand.addCard(shoe.getNextCard(true));
        return hand;
    }
    
    public void addCard(Card card) {
        if (Card.CA.equals(card)) {
            this.hasAces = true;
        }
        this.cards.add(card);
    }
    
    public boolean canSplit() {
        // splittable iff has exactly 2 cards and the cards have the same value
        return (this.cards.size() == 2)
            && (this.cards.get(0).getValue() == this.cards.get(1).getValue());
    }
    
    public void performDouble(Shoe deck) {
        if (this.hittable) {
            this.hittable = false;
            this.doubleable = false;
            this.bet = this.bet * 2;
            this.addCard(deck.getNextCard(true));
        }
    }
    
    public void performHit(Shoe deck) {
        if (hittable) {
            this.doubleable = false;
            this.addCard(deck.getNextCard(true));
        }
    }
    
    public Hand performSplit(Shoe deck) {
        if (!isSplittable() || !canSplit()) {
            throw new RuntimeException("attempt to split an unsplittable hand");
        }
        Card card = this.cards.get(1);
        this.cards.remove(card);
        this.addCard(deck.getNextCard(true));
        this.splittable = false;
        Hand newHand = new Hand();
        newHand.playerStats = this.playerStats;
        newHand.addCard(card);
        newHand.addCard(deck.getNextCard(true));
        newHand.bet = this.getBet();
        newHand.splittable = false;
        return newHand;
    }
    
    /**
     * Gets the highest total less than or equal to 21.
     * 
     * @return the best total value of the hand
     */
    public int getBestTotal() {
        int numAces = 0;
        int total = 0;
        for(Card card : this.cards) {
            if (Card.CA.equals(card)) {
                numAces++;
            } else {
                total += card.getValue();
            }
        }
        // handle aces: at *most* one ace can expand to 11. All others will be 1
        if (numAces > 0) {
            total += (numAces - 1); // all aces after the first equal 1
            // then see if the first ace expands to 11
            if (total < 11) {
                total += 11;
            } else {
                total++;
            }
        }
        return total;
    }
    
    public void scoreBlackjack() {
        PlayerStats playerStats = this.getPlayerStats();
        playerStats.incrBlackjacks();
        playerStats.incrWins();
        playerStats.incrCash(this.getBet());
        playerStats.incrCash(this.getBet()/2);
    }
    
    public void scoreLoss() {
        PlayerStats playerStats = this.getPlayerStats();
        playerStats.incrLosses();
        playerStats.incrCash(0-this.getBet());
    }
    
    public void scorePush() {
        PlayerStats playerStats = this.getPlayerStats();
        playerStats.incrPushes();
    }
    
    public void scoreWin() {
        PlayerStats playerStats = this.getPlayerStats();
        playerStats.incrWins();
        playerStats.incrCash(this.getBet());
    }
    
    @Override
    public String toString() {
        if (this.getBet() != 0) {
            return String.format("cards: %s; total: %d; bet: %d", this.cards, this.getBestTotal(), this.getBet());
        } else {
            return String.format("cards: %s; total: %d", this.cards, this.getBestTotal());
        }
    }
    
    // ==================================================
    // Simple getters/setters
    // ==================================================
    public int getBet() {
        return this.bet;
    }
    
    public Card getCard(int i) {
        return this.cards.get(i);
    }

    public int getCardCount() {
        return this.cards.size();
    }

    public boolean getHasAces() {
        return this.hasAces;
    }

    public PlayerStats getPlayerStats() {
        return this.playerStats;
    }

    public boolean isDoubleable() {
        return this.doubleable;
    }
    
    public boolean isHittable() {
        return this.hittable;
    }

    public boolean isSplittable() {
        return this.splittable;
    }

}
