package uta.cse3310;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserEvent {

    public enum UserEventType {
        NAME, STAND, DRAW, CALL, FOLD, RAISE, READY,NULL,STARTAMOUNT,TEXT;
        //NAME, STAND, HIT, CALL, RAISE, FOLD, BET, NULL;
        //NAME, STAND, DRAW, CHECK, BET, CALL, FOLD, RAISE, NULL;
        //Stand means don't swap cards
        //Draw means swap some current cards for other cards
        //check means next player can bet and current player forfeits bet(only available during first bet)
        //bet chooses amount (only available once)
        //call matches current bet amount
        //fold means you give up
        //Raise means raise current bet
        private UserEventType() {
        }
    };

    UserEventType event;
    int playerID;
    String name;
    String payload;
    String text;

    public UserEvent() {
    }

}