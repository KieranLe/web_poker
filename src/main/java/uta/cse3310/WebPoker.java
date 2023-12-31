// This code is based upon, and derived from the this repository
//            https:/thub.com/TooTallNate/Java-WebSocket/tree/master/src/main/example

// http server include is a GPL licensed package from
//            http://www.freeutils.net/source/jlhttp/

/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

package uta.cse3310;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class WebPoker extends WebSocketServer {

  private int numPlayers;
  private Game game;
  private int spotAvailable;//Out of players because I don't plan on removing players
  private int totalSpots = 0;//Will keep track of amount of players(the class) in game
  // to protect the game object from concurrent access
  private Object mutex = new Object();
  private void setNumPlayers(int N) {
    numPlayers = N;
  }

  public WebPoker(int port) throws UnknownHostException {
    super(new InetSocketAddress(port));
  }

  public WebPoker(InetSocketAddress address) {
    super(address);
  }

  public WebPoker(int port, Draft_6455 draft) {
    super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    if(numPlayers<4)
    {
      System.out.println(
          conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected");

      // Since this is a new connection, it is also a new player
      numPlayers = numPlayers + 1; // player id's start at 0
      if (numPlayers == 0) {
        System.out.println("starting a new game");
        //It's possible that a game should be created when numPlayers ==2 in order to prevent events from going into Game. 
        //However Game might need to be created in order for Name button and message to work. I'll need to look into it
        game = new Game();
      }
      for(int x = 0; x<game.players.size();x++)
      {
        if(game.players.get(x).left == true)
        {
          spotAvailable = x;
          break;
        }
        else
        {
          spotAvailable = numPlayers;
        }
      }
      conn.setAttachment(spotAvailable);
      // this is the only time we send info to a single client.
      // it needs to know it's player ID.
      conn.send(game.players.get(spotAvailable).asJSONString());
      synchronized (mutex) {
        game.addPlayer(spotAvailable);
      }
      //start game will be moved into Update

      // and as always, we send the game state to everyone
      synchronized (mutex) {
        broadcast(game.exportStateAsJSON());
        System.out.println("the game state" + game.exportStateAsJSON());
      }
    }
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    System.out.println(conn + " has closed");

    int idx = conn.getAttachment();
    numPlayers--;
    synchronized (mutex) {
      game.removePlayer(idx);

      System.out.println("removed player index " + idx);

      // The state is now changed, so every client needs to be informed
      broadcast(game.exportStateAsJSON());
      System.out.println("the game state" + game.exportStateAsJSON());

    }
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    synchronized (mutex) {
      // all incoming messages are processed by the game
      game.processMessage(message);
      // and the results of that message are sent to everyone
      // as the "state of the game"

      broadcast(game.exportStateAsJSON());
      game.text = "";
    }
    System.out.println(conn + ": " + message);
  }

  @Override
  public void onMessage(WebSocket conn, ByteBuffer message) {
    synchronized (mutex) {
      broadcast(message.array());
    }
    System.out.println(conn + ": " + message);
  }


  public class upDate extends TimerTask {
    @Override
    public void run() {
      if (game != null) {
        synchronized (mutex) {
        }
        totalSpots = 0;
        for(int x = 0;x<5;x++)
        {
          if(game.players.get(x).left == false)
          {
            totalSpots++;
          }
        }
        if(2<=game.currentPlayers && 5>=game.currentPlayers&& game.playersReady==totalSpots)
        {
          game.startGame(); 
          broadcast(game.exportStateAsJSON());
          game.playersReady =0;
          for(int x = 0;x<game.players.size();x++)//Resets players to unready
          {
            game.players.get(x).ready = false;
          }
        }
        if (game.update()) {
          broadcast(game.exportStateAsJSON());
          if(game.round == 3 && game.gameStarted == false)//fixes bug where result prints without showdown changes.
          {
            broadcast(game.exportStateAsJSON());
            game.round =-1;
            System.out.println(" second : the game state" + game.exportStateAsJSON());
          }

          System.out.println("the game state" + game.exportStateAsJSON());
        }

      }
    }
  }

  public static void main(String[] args) throws InterruptedException, IOException {

    // Create and start the http server

    HttpServer H = new HttpServer(8082, "./html");
    H.start();
    
    // create and start the websocket server

    int port = 8882;

    WebPoker s = new WebPoker(port);
    s.start();
    System.out.println("WebPokerServer started on port: " + s.getPort());

    // Below code reads from stdin, making for a pleasant way to exit
    BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      String in = sysin.readLine();
      s.broadcast(in);
      if (in.equals("exit")) {
        s.stop(1000);
        break;
      }
    }
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    ex.printStackTrace();
    if (conn != null) {
      // some errors like port binding failed may not be assignable to a specific
      // websocket
    }
  }

  @Override
  public void onStart() {
    System.out.println("Server started!");
    setConnectionLostTimeout(0);
    setConnectionLostTimeout(100);
    setNumPlayers(-1);
    // once a second call update
    new java.util.Timer().scheduleAtFixedRate(new upDate(), 0, 1000);
  }

}