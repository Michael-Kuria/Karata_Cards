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

    /*private Game(ArrayList<Player> players){

    }*/

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

    /**
     * Add a list of players
     * @param p list of players to be added
     */
    public void addAllPlayer(List<Player> p){
        players.addAll(p);
    }

    public void start(){

        initializeGame();
        Card c = table.getFirst();

        System.out.println("First Card : " + c);
        System.out.println("Number of players =" + players.size());
        boolean playersAreAvailable = players.size() > 1;


        while(on && playersAreAvailable){

            int index = 0; // used during reversing the game

            for(Player p : players){

                Card[] cards = p.place(c,escape);

                if(cards == null){

                    if(c.rank == 12 && escape == 1){
                        reversePlayers(index);
                        escape = 0;
                        break;

                    }else if((c.rank == 2 || c.rank == 3) && escape == 1){
                        p.pick(c.rank);
                        System.out.println(p + ": --------> has picked " +c.rank);
                    } else if(c.rank == 11 && escape == 1){
                        System.out.println(p +": --------> has been Jumped");
                    }else{
                        p.pick();
                        System.out.println(p +": ---> was unable to play " + Card.revealCards(p.count,p.hand));
                    }

                    if(escape == 1){
                        escape = 0;
                    }

                }else{

                    // If validation fails, the player will receive all the cards back and a penalty of one card
                    int n = cards.length;

                    if(validate(cards,c)){


                        System.out.println(p +" : has played " + Card.revealCards(n,cards));

                        for(Card card: cards)
                            table.addFirst(card);



                        // if cards[0] is a question then it should be followed by an answer, if none is provided then the player should pick an extra card
                        if(isAQuestion(cards[0]) && isAQuestion(cards[n - 1])){
                            p.pick();
                        }


                        // update player's state
                        updatePlayerState(p,cards[n - 1]);


                        if(escape == 1 && requiresAReaction(c)){
                            escape = 0;
                        }else if(requiresAReaction(cards[n - 1])){
                            escape = 1;
                        }

                        // update the card on top
                        c = table.getFirst();

                    }else{
                        System.out.println(p +" : (played wrongly) had played " + Card.revealCards(n,cards));
                        p.addToHand(cards);
                        p.pick();
                        continue;

                    }
                    /*if(escape == 1 && cards[0].rank == 12 && c.rank == 12){
                        escape = 0;
                        c = table.getFirst();
                        continue;
                    }

                    c = table.getFirst();


                    if(c != null && requiresAReaction(c)){
                        escape = 1;
                    }*/

                }



                if(!on){
                    declareWinner();
                    return;
                }
                updateDeck();

                index ++;
            }
        }


    }

    /**
     * Initialize game by dealing 3 cards to each player and
     * placing a starting card on the table
     *
     */
    public void initializeGame(){

        System.out.println("Table size: " + table.size() +" Deck size: " + deck.getSize());
        int n = 3; // cards each player will have

        for(int i = 0; i < n; i ++){

            for(Player p : players){
                p.pick();
            }
        }

        for(Player p : players){
            System.out.println(p +" : begins with " + Card.revealCards(n,p.hand));
        }

        // avoid having a special card at the top
        Card c = deck.deal();
        while(!isAWinningCard(c)){
            deck.cards.add(c);
            deck.shuffle();
            c = deck.deal();
        }

        table.add(c);
    }


    /**
     * Restart the game
     */
    public void restart(){
        // return all cards on the table to the deck
        for(Card card : table){
            deck.cards.offer(card);
        }

        // return all cards in player's hand to the deck
        ArrayList<Player> players1 = new ArrayList<>();
        for(Player p: players){

            for(int i = 0; i < p.count; i ++){
                System.out.println(p +" :  card = "+ p.hand[i] +" count " + p.count);
                deck.cards.offer(new Card(p.hand[i]));
                p.hand[i] = null;
            }
            players1.add(new ComputerPlayer((ComputerPlayer) p));
        }


        deck.shuffle();
        on = true;

        //game = Game.getGame();

        /*game.table = new LinkedList<>();
        game.players = new ArrayList<>();
        game.addAllPlayer(players1);*/
        if(players == null){
            System.out.println("Deck is null");
        }

        if (game == null) {

            System.out.println("Game is null");
        }


        game.start();

    }



    /**
     * Check if the player has played correctly according to the game rules i.e if at all he has placed a card on the table
     *
     * @param cards the set of cards that the player has placed on the table, this can never be null
     * @param c the card that was already on the table.
     *
     * @return true if the player has played safely
     */

    public boolean validate(Card[] cards, Card c){

        /**
         * Rules that  I need to take care of:
         * -> if c is a reaction card expect a cards as response to that action
         * -> Otherwise the following rules will apply:
         *      - cards[0] should have rank similar to that of c or the suit should be similar, if not card[0] should be an ace
         *      - if cards[0] is a question card it aught to be followed by an answer
         *      - if cards.length is greater than one then cards[1] -> cards[cards.length - 1] should of the same rank, if cards[0] is
         *      a questions then it can be followed by another question card of similar rank or suit, then an answer.
         *
         */
        int n = cards.length;
        boolean isAce;
        // Checking cards that require reaction
        if(requiresAReaction(c) && escape == 1){

            if(c.rank == 2 || c.rank == 3){

                isAce = checkRank(1,cards);

                if(!isAce){
                    return checkRank(c.rank, cards);
                }else{
                    return checkRank(1,cards);
                }
            }

            return checkRank(c.rank, cards);
        }



        isAce = checkRank(1, cards[0]);

        // an ace can be placed regardless of the suit of rank on of {@Param c}
        if(isAce){
            // the entire cards should contain aces
            return checkRank(1, cards);

        }else{

            if(c.rank == cards[0].rank){

                if(isAQuestion(cards[0])){
                    return checkQuestion(cards);
                }else{
                    return checkRank(c.rank, cards);
                }
            }else{
                // the suit should be the same
                if(c.suit == cards[0].suit){

                    if(isAQuestion(cards[0])){
                        return checkQuestion(cards);
                    }else {
                        return checkRank(cards[0].rank, cards);
                    }
                }
            }
        }

        return false;

    }


    /**
     * A subroutine that is called by validate to ensure that the {@code cards} have the same rank
     * @param rank the expected rank
     * @param cards an array of cards that need to be checked
     * @return True if they are equal and false otherwise
     */
    public boolean checkRank(int rank, Card ... cards){
        int n = cards.length;

        for(int i = 0; i < n; i ++){
            if(rank != cards[i].rank)
                return false;
        }
        return true;
    }


    /**
     * A subroutine to be called by validate when cards[0] is a question.
     *
     * @param cards an array containing cards that need to be checked
     * @return True if rules have been followed and False otherwise
     */
    public boolean checkQuestion(Card ... cards){
        int n = cards.length;

        for(int i = 1; i < n; i ++){

            Card prevCard = cards[i - 1]; // previous card
            Card curr = cards[i]; // current card

            if(isAQuestion(curr)){

                if(curr.rank == prevCard.rank)
                    continue;
                else{

                    if(curr.suit == prevCard.suit)
                        continue;
                    else
                        return false;
                }

            }else{

                if(isAQuestion(prevCard)){
                    if(curr.suit == prevCard.suit) {
                        continue;
                    }else{
                        return false;
                    }
                }else {

                    if(prevCard.rank != curr.rank)
                        return false;
                }

            }


        }
        return true;


    }




    /**
     *  {J,K,2,3} are sets of cards that an opponent will have to react.
     *
     * @param c Card that we are whether it is a reaction Card
     * @return true if it's special and false otherwise
     */
    public boolean requiresAReaction(Card c){

        return c.rank == 2 || c.rank == 3|| c.rank == 11 || c.rank == 12;

    }

    /**
     * {8 and Q} are question cards that will require an answer
     * @param c
     * @return true if it's a question card and false otherwise
     *
     */
    public boolean isAQuestion(Card c){

        return c.rank == 8 || c.rank == 13;

    }

    /**
     * The set of cards that exclude a questionCard and a Card that requires a reaction are the winning cards.
     *
     * This function will be called when verifying if a player has won
     * @param c
     * @return True if it can win and false otherwise
     */
    public boolean isAWinningCard(Card c){

        return !requiresAReaction(c) && !isAQuestion(c) && !(c.rank == 1);

    }


    /**
     * In case of a Kickback(K) reverse the players list.
     * Kickback has it's effect only when there are more than 2 players
     *
     * @param i is the index of the player who was intended to play
     */
    public void reversePlayers(int i){
        // i - 1 should be the last person to be added
        ArrayList<Player> newList = new ArrayList<>();
        int n = players.size();

        if(n > 2){

            if(i  == 0){
                for(int j = n - 2; j >= 0; j --){
                    newList.add(players.get(j));
                }
                newList.add(players.get(n - 1));

            }else {

                for(int j = i - 2; j >= 0; j --){
                    newList.add(players.get(j));
                }

                for(int j = n - 1; j >= i - 1; j -- ){
                    newList.add(players.get(j));
                }

            }


            System.out.println("Game reversed ");
            players = newList;
        }

    }

    /**
     * This subroutine will be called when a player has placed some cards on the table to update the state of the player.
     * If the player has no more cards on his hand, then if he has placed a winning card update his state to winner if  no player is Card-less
     * otherwise update to card-less
     *
     * @param p the player whose state is to be updated
     * @param c the last card placed
     */
    public void updatePlayerState(Player p, Card c){
        if(p.count == 0){
            if(isAWinningCard(c)){

                for(Player p1 : players){
                    if(p1.state == State.CARDLESS ){
                        return;
                    }
                }
                p.state = State.WINNER;
                on = false;
            }else{

                p.state = State.CARDLESS;
                //System.out.println(p +": is CARDLESS > > > > > > > > > > > > > > >  ");
            }
        }
    }

    /**
     * If the deck size is less than 5 then transfer all the cards on the table except the first on the deck
     */
    public void updateDeck(){

        if(deck.getSize() == 0 && table.size() < 3){
            restart();
            return;
        }

        if(deck.cards.size() < 10){
            System.out.println("Table size: " + table.size() +" Deck size: " + deck.getSize());
            Card c = table.removeFirst();

            for(Card card: table){
                if(card != null){
                    deck.cards.offer(card);
                }

            }
            table = new LinkedList<>();
            table.add(c);
            deck.shuffle();
            System.out.println("After: Table size: " + table.size() +" Deck size: " + deck.getSize());
        }
    }

    /**
     * Declare the winner
     */
    public void declareWinner(){
        Player winner = null;

        System.out.println("                        ||                     ");
        System.out.println("                        ||                     ");
        System.out.println("                        ||                     ");
        System.out.println("                        \\/                     ");
        System.out.println("                        \\/                     ");


        for(Player p: players){

            if(p.state == State.WINNER){
                winner = p;
            }else{
                System.out.println(p +" : was left with "+ Card.revealCards(p.count,p.hand));
            }
        }

        System.out.println(winner +": is the WINNER ");

    }

    public static void main(String [] args){

        Game game = new Game();

        for(int j = 1; j < 4; j ++ ){
            game.addPlayer(new ComputerPlayer(j +" "));
        }


        game.start();
        System.out.println();
        System.out.println("Table " + game.table.size() );//+ "ON TOP " + game.table.getFirst());
        System.out.println("Deck " + game.deck.getSize());

    }



}

