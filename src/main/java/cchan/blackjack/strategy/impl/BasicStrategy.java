package cchan.blackjack.strategy.impl;

import cchan.blackjack.model.Action;
import cchan.blackjack.model.Card;
import cchan.blackjack.model.Hand;
import cchan.blackjack.shoe.Shoe;
import cchan.blackjack.strategy.Strategy;

/**
 * Implementation of basic strategy.
 * @see <a href="http://en.wikipedia.org/wiki/Blackjack#Basic_strategy">http://en.wikipedia.org/wiki/Blackjack#Basic_strategy</a>
 */
public class BasicStrategy implements Strategy {

    @Override
    public int getNextBet(Shoe deck, int initialBet, int lastBet) {
        return initialBet; // this strategy always bets the same amount
    }

    @Override
    public Action getAction(Hand hand, Card dealerShows, Shoe deck) {
    	Action action = null;
        if (hand.canSplit() && hand.isSplittable()) {
            action = getActionPair(hand, dealerShows, deck);
        } else if (hand.getHasAces()) {
            action = getActionSoft(hand, dealerShows, deck);
        } else {
            action = getActionHard(hand, dealerShows, deck);
        }
        if (Action.DOUBLE.equals(action) && !hand.isDoubleable()) {
        	action = Action.HIT;
        }
        if (Action.HIT.equals(action) && !hand.isHittable()) {
        	action = Action.STAY;
        }
        return action;
    }

    public Action getActionHard(Hand hand, Card dealerShows, Shoe deck) {
        int total = hand.getBestTotal();
        if (total >= 17) {
            return Action.STAY;
        } else if (total < 17 && total > 12) {
            if (dealerShows.getValue() < 7) {
                return Action.STAY;
            } else {
                return Action.HIT;
            }
        } else if (total == 12) {
            if (dealerShows.getValue() >= 4
                    && dealerShows.getValue() <= 6) {
                return Action.STAY;
            } else {
                return Action.HIT;
            }
        } else if (total == 11) {
            if (Card.CA.equals(dealerShows)) {
                return Action.HIT;
            }
            return Action.DOUBLE;
        } else if (total == 10) {
            if (Card.CA.equals(dealerShows) || dealerShows.getValue() == 10) {
                return Action.HIT;
            }
            return Action.DOUBLE;
        } else if (total == 9) {
            if (dealerShows.getValue() >= 3 && dealerShows.getValue() <= 6) {
                return Action.DOUBLE;
            }
            return Action.HIT;
        } else if (total <= 8) {
            return Action.HIT;
        }

        return Action.STAY;
    }

    public Action getActionSoft(Hand hand, Card dealerShows, Shoe deck) {
        int total = hand.getBestTotal();
        if (total >= 19) {
            return Action.STAY;
        } else if (total == 18) {
            if (dealerShows.getValue() >= 3 && dealerShows.getValue() <= 6) {
                return Action.DOUBLE;
            } else if (dealerShows.getValue() >= 9 || Card.CA.equals(dealerShows)) {
                return Action.HIT;
            }
            return Action.STAY;
        } else if (total == 17) {
            if (dealerShows.getValue() >= 3 && dealerShows.getValue() <= 6) {
                return Action.DOUBLE;
            }
            return Action.HIT;
        } else if (total > 15) {
            if (dealerShows.getValue() >= 4 && dealerShows.getValue() <= 6) {
                return Action.DOUBLE;
            }
            return Action.HIT;
        } else {
            if (dealerShows.getValue() >= 5 && dealerShows.getValue() <= 6) {
                return Action.DOUBLE;
            }
            return Action.HIT;
        }
    }

    public Action getActionPair(Hand hand, Card dealerShows, Shoe deck) {
        Card card = hand.getCard(0);
        if (Card.CA.equals(card) || Card.C8.equals(card)) {
            return Action.SPLIT; // SHOULD BE SPLIT
        } else if (card.getValue() == 10) {
            return Action.STAY;
        } else if (Card.C9.equals(card)) {
            if (Card.C7.equals(dealerShows) || Card.CT.equals(dealerShows) || Card.CA.equals(dealerShows)) {
                return Action.STAY;
            }
            return Action.SPLIT;
        } else if (Card.C7.equals(card)) {
            if (dealerShows.getValue() >=2 && dealerShows.getValue() <= 7) {
                return Action.SPLIT;
            }
            return Action.HIT;
        } else if (Card.C6.equals(card)) {
            if (dealerShows.getValue() >=2 && dealerShows.getValue() <= 6) {
                return Action.SPLIT;
            }
            return Action.HIT;
        } else if (Card.C5.equals(card)) {
            if (dealerShows.getValue() >=2 && dealerShows.getValue() <= 9) {
                return Action.DOUBLE;
            }
            return Action.HIT;
        } else if (Card.C4.equals(card)) {
            if (dealerShows.getValue() >=5 && dealerShows.getValue() <= 6) {
                return Action.SPLIT;
            }
            return Action.HIT;
        } else if (Card.C3.equals(card) || Card.C2.equals(card)) {
            if (dealerShows.getValue() >=2 && dealerShows.getValue() <= 7) {
                return Action.SPLIT;
            }
            return Action.HIT;
        }
        return Action.STAY;
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
