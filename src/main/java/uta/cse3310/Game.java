package uta.cse3310;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uta.cse3310.UserEvent.UserEventType;


public class Game {

    ArrayList<Player> players = new ArrayList<>();
    int turn; // player ID that has the current turn
    int round = -1;//current round
    int playersReady;//Amount of players ready
    int playersFolded;//tracks amount of players who have folded
    int playersLeft;//tracks amount of players who have left
    boolean gameStarted = false;//Will keep track if gamestarted so game state only prints at showdown
    transient int startingPlayers;//Will be important if a player leaves(prob not needed anymote)
    int currentPlayers;//Keeps track of current players in game
    //int pot;//Total amount of money for winner
    //pot will be implemented in Dealer
    Dealer dealer = new Dealer();
    //Will be used to track if state has changed
    transient boolean tempState = false;
    //choice will be used to see choices made by players in the different rounds. They will be put into the rounds functions inorder to manipulate different players
    transient UserEvent choice = new UserEvent();

    String text = "";


    public String exportStateAsJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void addPlayer(int id) {
        players.get(id).left = false;
        players.get(id).folded = true;//incase joins in game
        startingPlayers++;//incase joins in game
        currentPlayers++;
    }

    public void removePlayer(int playerid) {
        // given it's player number, this method
        // deletes the player from the players array
        // and does whatever else is needed to remove
        // the player from the game.
        players.get(playerid).left = true;
        currentPlayers--;
        playersLeft++;
    }

    public void processMessage(String msg) {

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        // take the string we just received, and turn it into a user event
        UserEvent event = gson.fromJson(msg, UserEvent.class);
        //choice is the event we want to check for in rounds
        choice = event;
        if(event.event == UserEventType.TEXT)
        {
            text = event.text; 
        }
        
        if (event.event == UserEventType.NAME) {
            players.get(event.playerID).SetName(event.name);
        }
        if (event.event == UserEventType.STARTAMOUNT) {
            try{
                Integer.parseInt(choice.payload);
                players.get(event.playerID).startingMoney = Integer.parseInt(choice.payload);
            }
            catch(NumberFormatException e)//will set default to 100 if a incorrect number is inputed. Should prob change to giving an error and retyping
            {
                players.get(event.playerID).startingMoney = 100;
            }
            if(Integer.parseInt(choice.payload)<0)//will set default to 100 if a incorrect number is inputed. Should prob change to giving an error and retyping
            {
                players.get(event.playerID).startingMoney = 100;
            }
        }


        //In html only prints second betting round state so for no i'm gonna have this turn == -1 to send final game state
        if(round == -1&& choice.event == UserEventType.READY && players.get(choice.playerID).ready == false)//If a player is ready and hasnt readied before
        {
            players.get(choice.playerID).ready = true;
            playersReady++;
            choice.event = UserEventType.NULL;
        }

        if(choice.event != UserEventType.NAME && players.get(turn).folded != true)
        {
            if(round == 0)
            {
                if(choice.event != UserEventType.NULL && choice.playerID == turn)
                {
                    bettingRound();
                    tempState=true;
                    System.out.println("Betting round done :)");
                }
            }
            else if(round == 1)
            {
                if(choice.event != UserEventType.NULL && choice.playerID == turn)
                {
                    drawRound();
                    tempState=true;
                    System.out.println("Draw round done :)");
                }
            }
            else if(round == 2)
            {
                if(choice.event != UserEventType.NULL && choice.playerID == turn)
                {
                    bettingRound();
                    tempState=true;
                    System.out.println("Second betting round done :)");
                }
            }
        }

    }

    public boolean update() {
        // this function is called on a periodic basis (once a second) by a timer
        // it is to allow time based situations to be handled in the game
        // if the game state is changed, it returns a true.
        boolean changeState = false;
        //these if statments checks what round it is in order to manipulate data given from player making a choice. They check if all players are done
        //and returns true to show change in state of game. 
        if(tempState == true)
        {
            changeState = tempState;
            tempState = false;
        }
        
        //If player folded keeps skipping their turn
        if(round == 3 && gameStarted == true)
        {
            showdown();
            tempState=true;
            gameStarted = false;
            playersFolded = 0;
            playersLeft = 0;
            System.out.println("Game is done :)");
        }
        if(round != -1 && round != 3&& gameStarted==true &&(playersFolded == startingPlayers-1 || playersLeft == startingPlayers-1))
        {
            round = 3;
            showdown();
            tempState=true;
            gameStarted = false;
            playersFolded = 0;
            playersLeft = 0;
            System.out.println("Game is done :)");
        }
        if(round != -1 && round != 3 && players.get(turn)!=null && (players.get(turn).folded == true ||players.get(turn).left==true))
        {
            turn++;
            if(turn > startingPlayers-1)
            {
                round++;
                turn = 0;
            }
            changeState =true;
        }
        
        return changeState;
        // expecting that returning a true will trigger a send of the game
        // state to everyone

    }

    public Game() {
        System.out.println("creating a Game Object");
        //turn correlates with playerId to see whos turn it is
        turn =0; 
        //round represents current round
        round =-1;
        //pot holds total amount of player bets
        //pot=0;
        //NULL is used to make sure events aren't repeatedly implemented in Update
        choice.event = UserEventType.NULL;
        //Dealer will be used in draw round in order to implement draw
        for(int x = 0; x<5;x++)
        {
            Player player = new Player(x,dealer);
            players.add(player);
        }
    }

    public void startGame()
    {
        System.out.println("Game has started");
        dealer.fillDeck();
        gameStarted = true;
        //prob need to loop to set starting amounts at beginning of game for now will make it 100
        for(int x =0; x< players.size();x++)
        {
            players.get(x).money =players.get(x).startingMoney;
            players.get(x).Won =false;
            players.get(x).folded = false;
            dealer.createHand(players.get(x).Cards);
            System.out.print("Player " + x + ":" + players.get(x).money);
        }
        startingPlayers = currentPlayers;
        turn =0;
        dealer.totalBet = 0;
        dealer.pot = 0;
        round = 0;
        //playersReady will turn into 0 in update in WebPoker in order to get index to print

    }

    public boolean bettingRound()
    {
        //Bettinground will deal with removing money from player(if needed) and putting it in the pot
        //Will rely on methods in Player
        //money will eventually change for state of game sent after clicking button
        //tf is true if the event actually gets chosen
        boolean tf = false;
        if(choice.event == UserEventType.CALL)//Will change for betting accordingly
        {
            players.get(turn).playerCall(dealer);
            turn++;
        }
        else if(choice.event == UserEventType.RAISE)//Will change for betting accordingly
        {
            int money = Integer.parseInt(choice.payload);
            try {//If not correct input just default to 100
                money = Integer.parseInt(choice.payload);
            } catch (Exception e) {
                money = 0;
            }
            players.get(turn).playerRaise(money, dealer);
            turn++;
        }
        else if(choice.event == UserEventType.FOLD)
        {
            players.get(turn).playerFold();
            turn++;
            playersFolded++;
        }

        if(turn >= startingPlayers)
        {
            //next round
            tf = true;
            round++;
            turn = 0;
        }
        choice.event = UserEventType.NULL;
        return tf;
        
    }

    public boolean drawRound()
    {
        //drawRound will deal with removing player cards for cards in the deck in Dealer(if needed)
        boolean tf = false;
        //discard will probably hold the indexes for discards swapped cards in hand(depending on button implementation)
        ArrayList<Integer> discard = new ArrayList<Integer>();
        //addeded to discard for testing purposes
        //discard.add(0);
        //discard.add(1);
        //Stand means to not draw so prob don't need anything in the if statement
        if(choice.event == UserEventType.STAND)
        {
            //Pretty sure nothing happens if player doesn't discard
            turn++;
        }
        else if(choice.event == UserEventType.DRAW)
        {
            String arr = choice.payload;
            String[] tokens = arr.split(",");
            for(int i = 0; i < tokens.length ; i++){
                discard.add(Integer.parseInt(tokens[i]));
            }
            if(discard.size()!= 0)
            {
                //Use draw and discard in dealer(need event button before implementation in order to see how to put them in the methods)
                players.get(turn).playerDiscard(discard,dealer);
                players.get(turn).playerDraw(discard,dealer);
            }
            turn++;
        }
        if(turn >= startingPlayers)
        {
            //next round
            tf = true;
            round++;
            turn = 0;
        }
        choice.event = UserEventType.NULL;
        return tf;
    }

    public void showdown()
    {
        //Show down will compare hands of all eligible players(I think players that fold don't count)
        //Winner gets the pot added to their personal player amount
        //hand1 will hold highest hand
        Hand hand1 = new Hand();
        //hand2 will hold current players hand in loop
        Hand hand2 = new Hand();
        //holds index of winner(or first winner)
        int winner = 0;
        //winners holds indexes of winners
        ArrayList<Integer> winners = new ArrayList<>();

        //checks for folds
        ArrayList<Player> eligiblePlayers = new ArrayList<>();
        for(int x = 0; x< players.size();x++)
        {
            if(players.get(x).folded == false && players.get(x).left == false)
            {
                eligiblePlayers.add(players.get(x));
            }
        }
        //for errors
        if(eligiblePlayers.size() != 0)
        {
            hand1.cards = eligiblePlayers.get(0).Cards;
            winner = eligiblePlayers.get(0).Id;
        }
        //Puts winners(if ties) in an arraylist by first finding someone with strongest hand. Then seeing if any other hands match it.
        for(int x = 1; x< eligiblePlayers.size();x++)
        {
            hand2.cards = eligiblePlayers.get(x).Cards;
            if(hand2.is_better_than(hand1)== true)
            {
                hand1.cards = hand2.cards;
                winner = eligiblePlayers.get(x).Id;

            }
        }
        winners.add(winner);
        //finds ties with winner
        for(int x = 1; x< eligiblePlayers.size();x++)
        {
            hand2.cards = eligiblePlayers.get(x).Cards;
            //need to adjust Hand for isequal
            if(hand2.is_equal(hand1)== true)
            {
                winners.add(eligiblePlayers.get(x).Id);
            }
        }
        //Holds amount to be given to each winner
        int amountWon = dealer.pot/winners.size();
        //Adds money to winners from dealer
        for(int x =0;x<winners.size();x++)
        {
            players.get(winners.get(x)).money += amountWon;
            players.get(winners.get(x)).Won = true;    
        }
        dealer.totalBet = 0;
        dealer.pot = 0;

        turn = 0;
        choice.event = UserEventType.NULL;
    }
}
