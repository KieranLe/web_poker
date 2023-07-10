package uta.cse3310;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uta.cse3310.Card.Value;
import uta.cse3310.Card;


public class Hand
{

    // marked transient they will not serialized / deserialized

    public Card[] cards;

	public enum HandType
	{
		RoyalFlush,
		StraightFlush,
		FourOfAKind,
		FullHouse,
		Flush,
		Straight,
		ThreeOfAKind,
		TwoPair,
		Pair,
		HighNumber
	}

	public HandType type;

    public Hand()
    {
		
    }

    public boolean is_better_than(Hand H)
    {
		HandType Hand1Score;
		HandType Hand2Score;

		H.Sort();
		this.Sort();

		Hand1Score = this.GetHandType();
		Hand2Score = H.GetHandType();

		if(Hand1Score.ordinal() < Hand2Score.ordinal())
		{
			return true;
		}

		if(Hand1Score.equals(Hand2Score))
		{
			int score1 = this.HighCard();
			int score2 = H.HighCard();


			if(score1 > score2)
			{
				return true;
			}
		}

        return false;
    }
   
	public boolean is_equal(Hand H)
	{
		return false;
	}
	
	public boolean Has(Card.Value value)
	{
		for (Card card : cards) {
			if(card.value == value)
				return true;
		}
		return false;
	}


	public boolean Has(Card.Value value, Card.Suite suite)
	{
		for (Card card : cards) {
			if(card.suite == suite && card.value == value)
				return true;
		}
		return false;
	}

	public void Sort()
	{
		//Short Java lambda for sorting the cards from highest to lowest
		Arrays.sort(cards, new Comparator<Card>(){
			@Override
			public int compare(Card C1, Card C2){
				return C2.value.compareTo(C1.value);
			}
		});
		
	}

	public HandType GetHandType()
	{
		if(RoyalFlush() == true)			return HandType.RoyalFlush;
		else if(StraighFlush() == true)		return HandType.StraightFlush;
		else if(FourOfAKind() == true)		return HandType.FourOfAKind;
		else if(FullHouse() == true)		return HandType.FullHouse;
		else if(Flush() == true)			return HandType.Flush;
		else if(Straight() == true)			return HandType.Straight;
		else if(ThreeOfAKind() == true)		return HandType.ThreeOfAKind;
		else if(TwoPair() == true)			return HandType.TwoPair;
		else if(Pair() == true)				return HandType.Pair;
		else								return HandType.HighNumber;
	}


	public boolean HandOfSameSuite()
	{

		Card.Suite suite = cards[0].suite;

		for (Card card : cards) {
			if(card.suite != suite)
				return false;
		}

		return true;
	}

	public boolean IsSequential()
	{
		for (int i = 0; i < 4; i++) {

			if(cards[i].value != cards[i + 1].value.Next())
			{
				return false;
			}
		}

		return true;
	}


	public boolean RoyalFlush()
	{
		//This is easy since the Royal Flush only consists of these values
		if(HandOfSameSuite() == true)
		{
			if(Has(Card.Value.ACE) &&
			   Has(Card.Value.KING) &&
			   Has(Card.Value.QUEEN) &&
			   Has(Card.Value.JACK) &&
			   Has(Card.Value.TEN))
			   {
				   return true;
			   }
		}

		return false;
	}

	public boolean StraighFlush()
	{
		//Any hand of the same suite + beign sequential is a Straight Flush
		if(HandOfSameSuite() == true && IsSequential() == true)
		{
			return true;
		}
		return false;
	}

	public boolean FourOfAKind()
	{
		//We just need to loop over the array for each card and count how many times we another card of the same value
		for (Card card : cards) {

			int counter = 0;
			for (Card card2 : cards) {
				if(card.value == card2.value)
				{
					counter++;
				}
				//If we see 4 different ones then this is a 4 of a kind
				//I am assuming this is a single deck emulation of poker so there is no need to check for a potential "5 of a kind"

				if(counter == 4)
				{
					return true;
				}
			}
		}



		return false;
	}

	public boolean FullHouse()
	{
		//3 of a kind + pair = full house

		return (ThreeOfAKind() && Pair());
	}

	public boolean Flush()
	{
		//Each card in hand is of the same suite = flush
		return HandOfSameSuite();
	}

	public boolean Straight()
	{
		//Straight flush except not of the same suit

		return (IsSequential() && !HandOfSameSuite());
	}

	public boolean ThreeOfAKind()
	{
		//Same as 4 of a kind except 3
		for (Card card : cards) {

			int counter = 0;
			for (Card card2 : cards) {
				if(card.value == card2.value)
				{
					counter++;
				}
			}
			//Normally we would want some logic here to make sure both 4 and 3 of a kind don't trigger
			//But if we check in decreasing hand rank then this will be impossible

			if(counter == 3)
			{
				return true;
			}
		}

		return false;
	}

	public boolean TwoPair()
	{
		//Check for 2 pairs of different values
		int pairCounter = 0;
		Card tempCard = null;

		for (Card card : cards) {
			//We need this temp card here to make sure we do not repeat a pair already found
			if(tempCard != null)
			{
				if(card.value == tempCard.value)
				{
					continue;
				}
			}

			//This is basically copy and paste of the 4 and 3 of a kind code except we do need to make sure the counter gets cut off at 2
			int counter = 0;
			for (Card card2 : cards) {
				if(card.value == card2.value)
				{
					counter++;
				}
			}

			if(counter == 2)
			{
				pairCounter++;
				tempCard = card;
			}
		}

		if(pairCounter == 2)
		{
			return true;
		}


		return false;
	}

	public boolean Pair()
	{

		//Same concept as 2 pairs except only 1
		int pairCounter = 0;
		Card tempCard = null;

		for (Card card : cards) {
			if(tempCard != null)
			{
				if(card.value == tempCard.value)
				{
					continue;
				}
			}

			int counter = 0;
			for (Card card2 : cards) {
				if(card.value == card2.value)
				{
					counter++;
				}
			}

			if(counter == 2)
			{
				pairCounter++;
				tempCard = card;
			}
		}

		if(pairCounter == 1)
		{
			return true;
		}


		return false;
	}

	public int HighCard()
	{

		if(this.Has(Card.Value.ACE))
		{
			return 14;
		}


		switch (cards[0].value) {
			case TWO:	return 2;
			case THREE: return 3;
			case FOUR:	return 4;
			case FIVE:	return 5;
			case SIX:	return 6;
			case SEVEN:	return 7;
			case EIGHT:	return 8;
			case NINE:	return 9;
			case TEN:	return 10;
			case JACK:	return 11;
			case QUEEN:	return 12;
			case KING:	return 13;
			default:	return 0;
		}
	}


}
