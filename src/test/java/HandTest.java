import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import cchan.blackjack.PlayerStats;
import cchan.blackjack.model.Card;
import cchan.blackjack.model.Hand;
import cchan.blackjack.shoe.Shoe;


public class HandTest {

	@Test
	public void testCreatePlayerHand() {
		Shoe shoe = EasyMock.createMock(Shoe.class);
		PlayerStats stats = EasyMock.createMock(PlayerStats.class);

		// add behaviors
		EasyMock.expect(shoe.getNextCard(true)).andReturn(Card.C8);
		EasyMock.expect(shoe.getNextCard(true)).andReturn(Card.C5);


		// replay
		EasyMock.replay(shoe);

		// call method to test
		Hand hand = Hand.createPlayerHand(stats, shoe, 10);

		Assert.assertEquals(hand.getBestTotal(), 13);
		Assert.assertFalse(hand.canSplit());

		// TODO: write more tests
	}

}
