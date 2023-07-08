package uta.cse3310;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import uta.cse3310.Card;

public class Dealer {
    transient uta.cse3310.Card deck[] = new Card[52];
    transient int taken = 0;
    //totalBet will hold the current bet for each player
    int totalBet = 0;
    //pot holds total amount of money betted
    int pot = 0;
    //Amount to be removed when a player calls

    public Dealer()
    {
        String[] value = {"ACE","TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "JACK", "QUEEN", "KING"};
        String[] suite = {"DIAMONDS", "SPADES", "CLUBS", "HEARTS"};
        int count = 0;
        for( int val = 0; val < 13; val++ )
        {
            for( int ste = 0; ste < 4; ste++ )
            {
                deck[count] = new Card();
                deck[count].value = Card.Value.valueOf(value[val]);
                deck[count].suite = Card.Suite.valueOf(suite[ste]);        
                count++; 
            }
        }

        List<Card> cards = Arrays.asList(deck);
        Collections.shuffle(cards);
        cards.toArray(deck);
    }

    public void fillDeck()
    {
        String[] value = {"ACE","TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "JACK", "QUEEN", "KING"};
        String[] suite = {"DIAMONDS", "SPADES", "CLUBS", "HEARTS"};
        int count = 0;
        for( int val = 0; val < 13; val++ )
        {
            for( int ste = 0; ste < 4; ste++ )
            {
                deck[count] = new Card();
                deck[count].value = Card.Value.valueOf(value[val]);
                deck[count].suite = Card.Suite.valueOf(suite[ste]);        
                count++; 
            }
        }

        List<Card> cards = Arrays.asList(deck);
        Collections.shuffle(cards);
        cards.toArray(deck);
     }

    public void createHand(Card Cards[])
    {
        // fillDeck();

        ArrayList<Integer> randomCard = new ArrayList<>();
        for ( int i = 0; i < 52; i++ )
        {
            randomCard.add(i);
        }

        Collections.shuffle(randomCard);

        for( int i = 0; i < 5; i++ )
        {
            Cards[i] = deck[randomCard.get(i)];
            taken++;
        }
    }

    public void draw(Card Cards[], ArrayList<Integer> discard)
    {
        for( int i = 0; i < discard.size(); i++ )
        {
            Cards[discard.get(i)] = deck[taken++];
        }
    }

    public void discard(Card Cards[], ArrayList<Integer> discard)
    {
        for( int i = 0 ; i < discard.size(); i++ )
        {
            Cards[discard.get(i)] = null;
        }
    }

    public void setBet(int money)
    {
        totalBet += money;
    }

    public int getBet()
    {
        return totalBet;
    }
    public int checkDeckRemaining()
    {
        List<Card> cards = Arrays.asList(deck);
        return cards.size();
    }

}