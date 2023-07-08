package uta.cse3310;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.util.ArrayList;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */

    Dealer deal = new Dealer();

    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    // Check to see if game starts with 52 cards 
    // Requirement NF-02
    @Test
    public void test52Card()
    {
        assertEquals(52, deal.checkDeckRemaining());
    }

    // Compare a Royal flush with Straight flush
    // Requirement NF-15
    @Test
    public void testCompareHands()
    {
        Hand hand_1 = new Hand();
        hand_1.cards = new Card[5];
        hand_1.cards[0] = new Card();
        hand_1.cards[0].suite = Card.Suite.valueOf("HEARTS");
        hand_1.cards[0].value = Card.Value.valueOf("TEN");
        hand_1.cards[1] = new Card();
        hand_1.cards[1].suite = Card.Suite.valueOf("HEARTS");
        hand_1.cards[1].value = Card.Value.valueOf("JACK");
        hand_1.cards[2] = new Card();
        hand_1.cards[2].suite = Card.Suite.valueOf("HEARTS");
        hand_1.cards[2].value = Card.Value.valueOf("QUEEN");
        hand_1.cards[3] = new Card();
        hand_1.cards[3].suite = Card.Suite.valueOf("HEARTS");
        hand_1.cards[3].value = Card.Value.valueOf("KING");
        hand_1.cards[4] = new Card();
        hand_1.cards[4].suite = Card.Suite.valueOf("HEARTS");
        hand_1.cards[4].value = Card.Value.valueOf("ACE");


        Hand hand_2 = new Hand();
        hand_2.cards = new Card[5];
        hand_2.cards[0] = new Card();
        hand_2.cards[0].suite = Card.Suite.valueOf("SPADES");
        hand_2.cards[0].value = Card.Value.valueOf("FOUR");
        hand_2.cards[1] = new Card();
        hand_2.cards[1].suite = Card.Suite.valueOf("SPADES");
        hand_2.cards[1].value = Card.Value.valueOf("FIVE");
        hand_2.cards[2] = new Card();
        hand_2.cards[2].suite = Card.Suite.valueOf("SPADES");
        hand_2.cards[2].value = Card.Value.valueOf("SIX");
        hand_2.cards[3] = new Card();
        hand_2.cards[3].suite = Card.Suite.valueOf("SPADES");
        hand_2.cards[3].value = Card.Value.valueOf("SEVEN");
        hand_2.cards[4] = new Card();
        hand_2.cards[4].suite = Card.Suite.valueOf("SPADES");
        hand_2.cards[4].value = Card.Value.valueOf("EIGHT");

        boolean result = hand_1.is_better_than(hand_2);

        // hand_1 is better than hand_2, should return True
        assertEquals(true, result);

    }

    // Comparing 2 hands with same rank 
    @Test
    public void testHighCardHands()
    {
        Hand hand_1 = new Hand();
        hand_1.cards = new Card[5];
        hand_1.cards[0] = new Card();
        hand_1.cards[0].suite = Card.Suite.valueOf("DIAMONDS");
        hand_1.cards[0].value = Card.Value.valueOf("ACE");
        hand_1.cards[1] = new Card();
        hand_1.cards[1].suite = Card.Suite.valueOf("HEARTS");
        hand_1.cards[1].value = Card.Value.valueOf("QUEEN");
        hand_1.cards[2] = new Card();
        hand_1.cards[2].suite = Card.Suite.valueOf("HEARTS");
        hand_1.cards[2].value = Card.Value.valueOf("NINE");
        hand_1.cards[3] = new Card();
        hand_1.cards[3].suite = Card.Suite.valueOf("SPADES");
        hand_1.cards[3].value = Card.Value.valueOf("FIVE");
        hand_1.cards[4] = new Card();
        hand_1.cards[4].suite = Card.Suite.valueOf("DIAMONDS");
        hand_1.cards[4].value = Card.Value.valueOf("THREE");


        Hand hand_2 = new Hand();
        hand_2.cards = new Card[5];
        hand_2.cards[0] = new Card();
        hand_2.cards[0].suite = Card.Suite.valueOf("HEARTS");
        hand_2.cards[0].value = Card.Value.valueOf("KING");
        hand_2.cards[1] = new Card();
        hand_2.cards[1].suite = Card.Suite.valueOf("SPADES");
        hand_2.cards[1].value = Card.Value.valueOf("SEVEN");
        hand_2.cards[2] = new Card();
        hand_2.cards[2].suite = Card.Suite.valueOf("HEARTS");
        hand_2.cards[2].value = Card.Value.valueOf("FIVE");
        hand_2.cards[3] = new Card();
        hand_2.cards[3].suite = Card.Suite.valueOf("DIAMONDS");
        hand_2.cards[3].value = Card.Value.valueOf("FOUR");
        hand_2.cards[4] = new Card();
        hand_2.cards[4].suite = Card.Suite.valueOf("SPADES");
        hand_2.cards[4].value = Card.Value.valueOf("TWO");

        boolean result = hand_1.is_better_than(hand_2);

        // hand_1 is better than hand_2, should return True
        assertEquals(true, result);

    }

    // Check to see if the remaining cards on hand of player 
    // match with the number cards they discarded 
    @Test
    public void testDiscard()
    {
        ArrayList<Integer> discardIndex = new ArrayList<Integer>();
        Player p1 = new Player(1,deal);
        deal.createHand(p1.Cards);
        discardIndex.add(1);
        discardIndex.add(2);
        discardIndex.add(3);

        p1.playerDiscard(discardIndex, deal);
        int result = p1.getNumberOfCards();

        // Disacard 3 cards, remaining should be 2
        assertEquals(2, result);
    }

    // Test draw cards
    @Test
    public void testDraw()
    {
        ArrayList<Integer> discardIndex = new ArrayList<Integer>();
        Player p1 = new Player(1,deal);
        deal.createHand(p1.Cards);
        discardIndex.add(1);
        discardIndex.add(2);
        discardIndex.add(3);

        p1.playerDiscard(discardIndex, deal);
        int result = p1.getNumberOfCards();

        // Disacard 3 cards, remaining should be 2
        assertEquals(2, result);

        // Draw back 3 cards, total should be 5
        p1.playerDraw(discardIndex, deal);
        assertEquals(5, p1.getNumberOfCards());
    }


    // Check to see if new player get deal 5 cards when joining game
    // Requirement
    @Test
    public void testNewPlayer()
    {
        Player Joe = new Player(7, deal);
        deal.createHand(Joe.Cards);
        assertEquals(5, Joe.getNumberOfCards());
    }

    // Add players test
    @Test
    public void testAddPlayer()
    {      
        Game game = new Game();
        game.addPlayer(1);
        game.addPlayer(2);
        game.addPlayer(3);

 
        int result = game.currentPlayers;
        
        assertEquals(3, result);
    }

    // Remove players test 
    @Test
    public void testRemovePlayer()
    {
        Game game = new Game();
        game.addPlayer(1);
        game.addPlayer(2);
        game.addPlayer(3);

        game.removePlayer(1);
        game.removePlayer(2);


        int result = game.currentPlayers;
        assertEquals(1, result);
    }

    // Test set name for player 
    @Test
    public void testSetName()
    {
        Player playerOne = new Player(1, deal);
        playerOne.SetName("Jane");

        assertEquals("Jane", playerOne.getName());
    }

    @Test
    public void testPlayerFold()
    {
        Player playerOne = new Player(1, deal);
        playerOne.playerFold();
        
        assertEquals(true, playerOne.getPlayerFoldStatus());

    }

}