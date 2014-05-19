package cchan.blackjack;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import cchan.blackjack.model.Action;
import cchan.blackjack.model.Card;
import cchan.blackjack.model.Hand;
import cchan.blackjack.shoe.Shoe;
import cchan.blackjack.shoe.impl.FiniteShoe;
import cchan.blackjack.strategy.Strategy;
import cchan.blackjack.strategy.impl.CountingStrategy;
import cchan.blackjack.strategy.impl.DealerStrategy;

public class Sim {

	// defaults

    private int initialBet = 10;

	private int numHands = 1000;

    private int startingCash = 1000;

    private Class playerStrategy = CountingStrategy.class;

    private Class shoeType = FiniteShoe.class;

    private int shoeDecks = 6;

    public static final void main(String[] argv) {
    	Options options = new Options();
    	options.addOption(OptionBuilder
    			.withDescription("full path to file containing configuration properties")
    			.hasArg()
    			.isRequired()
    			.create('p'));
    	Configuration config = null;
    	try {
    	    config = parseCommandLine(options, argv);
    	} catch (ParseException pe) {
    		System.err.println(pe.getMessage());
    		printUsage(options);
    		System.exit(1);
    	} catch (ConfigurationException ce) {
    		System.err.println(ce.getMessage());
    		printConfigurationInfo();
    		System.exit(1);
    	}
        createSim(config).runSim();
    }

    private static Sim createSim(Configuration config) {
    	Sim sim = new Sim();
        sim.initialBet = config.getInt("initialBet", sim.initialBet);
        sim.numHands = config.getInt("numHands", sim.numHands);
        sim.startingCash = config.getInt("startingCash", sim.startingCash);
        sim.shoeDecks = config.getInt("shoeDecks", sim.shoeDecks);
        String playerStrategyValue = config.getString("playerStrategy");
        try {
			if (StringUtils.isNotBlank(playerStrategyValue)) {
				sim.playerStrategy = Class.forName(String.format("cchan.blackjack.strategy.impl.%s", playerStrategyValue));
			}
			String shoeValue = config.getString("shoeType");
			if (StringUtils.isNotBlank(shoeValue)) {
				sim.shoeType = Class.forName(String.format("cchan.blackjack.shoe.impl.%s", shoeValue));
			}
		} catch (ClassNotFoundException cnfe) {
			System.err.println(cnfe.getMessage());
            System.exit(1);
		}
    	return sim;
    }

    private static Configuration parseCommandLine(Options options, String[] argv) throws ParseException, ConfigurationException {
        CommandLineParser parser = new GnuParser();
        CommandLine cmd = parser.parse(options, argv);
        Option propertyOption = options.getOption("p");
        if (propertyOption == null) {
        	throw new ParseException("Configuration property file is required.");
        }
        return new PropertiesConfiguration(cmd.getOptionValue('p'));
    }

    private static void printUsage(Options options) {
    	HelpFormatter helpFormatter = new HelpFormatter();
    	helpFormatter.printHelp("mvn exec:java -Dexec.args=\"-p config.properties\"", options);
    }

    private static void printConfigurationInfo() {
        // TODO
    	System.out.println("configure blackjack simulator");
    }

    public void runSim() {
        int lastBet = this.initialBet;

        PlayerStats playerStats = new PlayerStats(this.startingCash);
        PlayerStats dealerStats = new PlayerStats(0);

        Shoe shoe = null;
        Strategy strategy = null;
        try {
        	if (this.shoeType.equals(FiniteShoe.class)) {
        		shoe = (Shoe) this.shoeType.getConstructor(new Class[] {Integer.TYPE}).newInstance(new Object[] {this.shoeDecks});
        	} else {
        		shoe = (Shoe) this.shoeType.newInstance();
        	}
        	strategy = (Strategy) this.playerStrategy.newInstance();
        } catch (Exception e) {
        }

        Strategy dealerStrategy = new DealerStrategy();
        shoe.registerStrategy(strategy);
        Action action = null;
        LinkedList<Hand> hands = null;

        for(int i=0; i<this.numHands; i++) {
            shoe.shuffle();

            int bet = strategy.getNextBet(shoe, this.initialBet, lastBet);
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
