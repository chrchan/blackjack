package cchan.blackjack;

import java.util.LinkedList;
import java.util.List;

import cchan.blackjack.model.Action;
import cchan.blackjack.model.Card;
import cchan.blackjack.model.Hand;
import cchan.blackjack.shoe.Shoe;
import cchan.blackjack.shoe.impl.FiniteShoe;
import cchan.blackjack.strategy.Strategy;
import cchan.blackjack.strategy.impl.CountingStrategy;
import cchan.blackjack.strategy.impl.DealerStrategy;

public class Sim {

    private final static int STARTING_CASH = 1000;

    public static final void main(String[] argv) {
        new Sim().runSim();
    }

    public void runSim() {
        int numHands = 10000;
        int initialBet = 10;
        int lastBet = initialBet;

        PlayerStats playerStats = new PlayerStats(STARTING_CASH);
        PlayerStats dealerStats = new PlayerStats(0);

        Shoe shoe = new FiniteShoe(6);
        Strategy strategy = new CountingStrategy();
        Strategy dealerStrategy = new DealerStrategy();
        shoe.registerStrategy(strategy);
        Action action = null;
        LinkedList<Hand> hands = null;

        for(int i=0; i<numHands; i++) {
            shoe.shuffle();

            int bet = strategy.getNextBet(shoe, initialBet, lastBet);
            Hand playerHand = Hand.createPlayerHand(playerStats, shoe, bet);
            Hand dealerHand = Hand.createDealerHand(dealerStats, shoe);
            Card dealerShows = dealerHand.getCard(1);

            hands = new LinkedList<Hand>();
            hands.add(playerHand);

            System.out.println(String.format("==== Hand #%d; Dealer Shows: %s", i, dealerShows));
            playHand(strategy, hands, playerHand, shoe, dealerShows);

            // Dealer needs to play if there is at least one non-busted, non-blackjack player hand
            boolean dealerPlays = false;
            for(Hand hand : hands) {
                int playerTotal = hand.getBestTotal();
                boolean naturalBj = playerTotal == 21 && hand.getCardCount() == 2;
                boolean bust = playerTotal > 21;
                if (!bust && !naturalBj) {
                    dealerPlays = true;
                }
            }



            // dealer hand
            System.out.print("Dealer: ");
            if (dealerPlays) {
            	boolean bust = false;
                action = dealerStrategy.getAction(dealerHand, dealerShows, shoe);
                while (!Action.STAY.equals(action)) {
                    switch (action) {
                    case HIT:
                        System.out.print("HIT ");
                        dealerHand.performHit(shoe);
                        break;
                    }
                    action = dealerStrategy.getAction(dealerHand, dealerShows, shoe);
                    int dealerTotal = dealerHand.getBestTotal();
                    if (dealerTotal > 21) {
                        System.out.print("BUST! ");
                        bust = true;
                    }
                }
                if (!bust) {
                    System.out.print("STAY ");
                }
            }
            System.out.println(dealerHand);
            checkHands(hands, dealerHand);

            lastBet = playerHand.getBet();
        }
        System.out.println("============ FINAL STATS =============");
        System.out.println(String.format("Wins: %d", playerStats.getWins()));
        System.out.println(String.format("Losses: %d", playerStats.getLosses()));
        System.out.println(String.format("Pushes: %d", playerStats.getPushes()));
        System.out.println(String.format("Blackjack: %d", playerStats.getBlackjacks()));
        System.out.println(String.format("Total cash: %d", playerStats.getCash()));
    }

    private void playHand(Strategy strategy, List<Hand> hands, Hand playerHand, Shoe deck, Card dealerShows) {
        Hand splitHand = null;
        boolean bust = false;

        // player hand
        Action action = strategy.getAction(playerHand, dealerShows, deck);
        System.out.print("Player: ");
        while(!Action.STAY.equals(action)) {
            switch(action) {
            case DOUBLE:
                System.out.print("DOUBLE ");
                playerHand.performDouble(deck);
                action = Action.STAY;
                break;
            case HIT:
                System.out.print("HIT ");
                playerHand.performHit(deck);
                action = strategy.getAction(playerHand, dealerShows, deck);
                break;
            case SPLIT:
                System.out.print("SPLIT ");
                splitHand = playerHand.performSplit(deck);
                hands.add(splitHand);
                action = strategy.getAction(playerHand, dealerShows, deck);
                break;
            }
            int playerTotal = playerHand.getBestTotal();
            if (playerTotal > 21) {
                System.out.print("BUST! ");
                bust = true;
            }
        }
        if (!bust) {
        	System.out.print("STAY ");
        }
        System.out.println(playerHand);

        // handle the split hand
        if (splitHand != null) {
            playHand(strategy, hands, splitHand, deck, dealerShows);
        }
    }

    private void checkHands(List<Hand> hands, Hand dealerHand) {
        int dealerTotal = dealerHand.getBestTotal();

        for(Hand playerHand : hands) {
            int playerTotal = playerHand.getBestTotal();
            boolean naturalBj = playerTotal == 21 && playerHand.getCardCount() == 2;

            if (naturalBj) {
                // natural 21
                System.out.println("BLACKJACK!");
                playerHand.scoreBlackjack();
            } else if (playerTotal <= 21 && playerTotal > dealerTotal) {
                System.out.println(String.format("WIN! You: %d; Dealer: %d", playerTotal, dealerTotal));
                playerHand.scoreWin();
            } else if (playerTotal <= 21 && dealerTotal > 21) {
                System.out.println(String.format("DEALER BUST! You: %d; Dealer: %d", playerTotal, dealerTotal));
                playerHand.scoreWin();
            } else if (playerTotal <= 21 && playerTotal == dealerTotal) {
                System.out.println(String.format("PUSH You: %d; Dealer: %d", playerTotal, dealerTotal));
                playerHand.scorePush();
            } else {
                System.out.println(String.format("LOSE You: %d; Dealer: %d", playerTotal, dealerTotal));
                // every other case is a loss, so player loses bet
                playerHand.scoreLoss();
            }

            System.out.println(String.format("Cash: %d", playerHand.getPlayerStats().getCash()));
        }
    }

}
