package uta.cse3310;

public class Card
{
   public enum Suite
   {
      HEARTS,CLUBS,DIAMONDS,SPADES
   }

   public enum Value
   {
      ACE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,
      TEN,JACK,QUEEN,KING;

      public Value Next()
      {
         if(ordinal() == values().length-1)
         {
            return values()[values().length-1];
         }

         return values()[ordinal() + 1];
      }

   }  

    public Suite suite;
    public Value value;

    public Card()
    {
      
    }
}