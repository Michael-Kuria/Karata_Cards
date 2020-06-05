package com.karata_cards;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game {

    public static Game game = null;
    public ArrayList<Player> players;
    public Deck deck;
    public LinkedList<Card> table; // playing cards
    public boolean on; // is the game still on or has someone already won
    public int escape = 0;


    private Game(){
        players = new ArrayList<>();
        deck = Deck.getDeck();
        table = new LinkedList<>();
        on = true;

    }

    /**
     * singleton Game class
     */
    public static Game getGame(){
        if(game == null){
            game = new Game();
        }
        return game;
    }

    /**
     * Add a single player to the game
     */
    public void addPlayer(Player player){
        players.add(player);
    }

    public void start(){

        initializeGame();
        Card c = table.getFirst();

        System.out.println("First Card : " + c);


        while(on){

            int index = 0;

            for(Player p : players){
                Card[] card = p.place(c,escape);

                if(card == null){

                    if(c.rank == 12 && escape == 1){
                        reversePlayers(index);

                    }else if((c.rank == 2 || c.rank == 3) && escape == 1){
                        p.pick(c.rank);
                        System.out.println(p + ": --------> has picked " +c.rank);
                    } else if(c.rank == 11 && escape == 1){
                        System.out.println(p +": --------> has been Jumped");
                    }else{
                        System.out.println(p +": --------> was unable to play ");
                        p.pick();
                    }

                    if(escape == 1){
                        escape = 0;
                    }

                }else{
                    String prt = p +": ";
                    for(Card card1: card){
                        if(card1 != null)
                            table.addFirst(card1);
                        else
                            System.out.println(p + " >>>>>>>>>>>>>>>>>>>>>>>>>>>> played with a null value " + p.count);
                        prt += card1 +" ";

                    }


                    p.updateState();

                    if(p.state == State.WINNER){

                        on = false;
                    }

                    c = table.getFirst();


                    if(c != null && specialCard(c)){
                        escape = 1;
                    }
                    System.out.println(prt + " with " + p.count +" Cards " + escape);

                }



                if(!on){
                    declareWinner();
                    return;
                }
                updateDeck();

                index ++;
                //System.out.println(deck.cards.size());
            }
        }

      /*  while(on){
            for(Player p: players){

                Card card = p.place(c);
                if(card != null){


                    table.add(card);
                    System.out.println(p +" : " + card);
                    p.updateState();

                    if(p.state == State.WINNER){
                        on = false;
                    }

                    c = card;
                }

                if(!on){
                    declareWinner();
                    return;
                }
                updateDeck();
            }
        }*/

    }

    /**
     * Initialize game by dealing 3 cards to each player and
     * placing a starting card on the table
     *
     */
    public void initializeGame(){

        System.out.println("Table size: " + table.size() +" Deck size: " + deck.cards.size());
        for(int i = 0; i < 3; i ++){

            for(Player p : players){
                p.pick();
            }
        }

        // avoid having a special card at the top
        Card c = deck.deal();
        while(!specialCardForOpponent(c)){
            deck.cards.add(c);
            deck.shuffle();
            c = deck.deal();
        }

        table.add(c);
    }

    /**
     * Check if c is a special card i.e {Q,J,K,A,8}
     * @param c
     * @return true if it's special and false otherwise
     */
    public boolean specialCard(Card c){

        return c.rank == 2 || c.rank == 3|| c.rank == 11 || c.rank == 12;

    }

    /**
     * Incases of a Kickback reverse the players list
     */
    public void reversePlayers(int i){
        ArrayList<Player> newList = new ArrayList<>();

        if(i > 0){
            for(int j = i - 1; j >= 0; j --){
                newList.add(players.get(j));
            }

        }

        for(int j = players.size() - 1; j >= i; j -- ){
            newList.add(players.get(j));
        }
        System.out.println("Game reversed ");
        players = newList;

    }

    public boolean specialCardForOpponent(Card c){

        if(c.rank == 2 || c.rank == 3 || c.rank == 11 || c.rank == 12){
            return true;
        }
        return false;
    }

    /**
     * If the deck size is less than 5 then transfer all the cards on the table except the first on the deck
     */
    public void updateDeck(){
        if(deck.cards.size() < 10){
            System.out.println("Table size: " + table.size() +" Deck size: " + deck.cards.size());
            Card c = table.removeFirst();

            for(Card card: table){
                if(card != null){
                    deck.cards.offer(card);
                }

            }
            table = new LinkedList<>();
            table.add(c);
            deck.shuffle();
        }
    }

    /**
     * declare the winner, if one of the players have won
     */
    public void declareWinner(){
        for(Player p: players){
            p.printHand();
            if(p.state == State.WINNER){
                System.out.println(p.name +" is the winner");
            }
        }

    }

    /**
     * Check if the player has played correctly according to the game rules i.e if at all he has placed a card on the table
     *
     * @param p The player who just played
     * @param cards the cards that he has place on top
     * @param card the card that was on top before the player played
     * @return true if the player has played safely
     */

    public boolean validate(Player p, Card[] cards, Card card){

        if(card.rank == 1){
            // request a card
        }

        // refuse to be jumped J
        if(card.rank == 11 && cards[0].rank != 11){
            return false;

        }

        // Refuse to deal 2
        if((card.rank == 2 ) && cards[0].rank > 2 ){
            return false;
        }

        // refuse to deal 3
        if(card.rank == 3 && (cards[0].rank != 1 || cards[0].rank != 3)){
            return false;
        }

        // Check to see if the value/ the flower match
        if(cards[0].suit != card.suit && cards[0].value != card.value){
            return false;
        }


        int n = cards.length;

        // if the player has played an 8 check to confirm that he has placed an extra card
        if(cards[0].rank == 8 || cards[0].rank == 13){
            if(n < 2){
                return false;
            }

            if(cards[1].rank == cards[0].rank){
                if(cards[n - 1].suit != cards[n - 2].suit && cards[n - 1].rank != cards[n - 2].rank ){
                    return false;
                }
            }

        }



        for(int i = 1; i < n; i ++){

            //if(cards[i - 1].)
        }

        return true;

    }


    public boolean validate2(Player p, Card[] cards, Card card){
        return true;
    }



    public static void main(String [] args){



        //for(int i = 0; i < 100; i ++){
            //System.out.println( i + "================================================================================================================================");
            Game game = new Game();

            for(int j = 1; j < 4; j ++ ){
                game.addPlayer(new ComputerPlayer(j +" "));
            }

            game.start();
            game.declareWinner();
            System.out.println("Table " + game.table.size());
            System.out.println("Deck " + game.deck.cards.size());
            //System.out.println( "=============================================================================================================================");

    }



}

