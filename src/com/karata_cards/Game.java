package com.karata_cards;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

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

    public void start(){

        initializeGame();
        Card c = table.getFirst();

        System.out.println("First Card : " + c);


        while(on){

            int index = 0;

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
                        System.out.println(p +": --------> was unable to play " + p.printHand());
                    }

                    if(escape == 1){
                        escape = 0;
                    }

                }else{

                    if(validate(cards,c)){
                        printCardsPlayed(p,cards);

                        for(Card card: cards)
                            table.addFirst(card);

                    }else{
                        System.out.println(p+": has played wrongly");
                        p.addToHand(cards);
                        p.pick();
                        continue;

                    }


                    p.updateState();

                    if(p.state == State.WINNER){

                        on = false;
                    }

                    if(escape == 1 && cards[0].rank == 12 && c.rank == 12){
                        escape = 0;
                        c = table.getFirst();
                        continue;
                    }

                    c = table.getFirst();


                    if(c != null && requiresAReaction(c)){
                        escape = 1;
                    }


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
        for(int i = 0; i < 3; i ++){

            for(Player p : players){
                p.pick();
            }
        }

        for(Player p : players){
            System.out.println(p +": Cards at hand "+ p.printHand());
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
    private void restart(){
        game = null;
        deck = null;
        game = Game.getGame();

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
     * If the deck size is less than 5 then transfer all the cards on the table except the first on the deck
     */
    public void updateDeck(){
        if(deck.getSize() == 0 && table.size() < 3){

        }
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
            System.out.println(p.printHand());
            if(p.state == State.WINNER){
                System.out.println(p.name +" is the winner");
            }
        }

    }

    /**
     * Neatly print the cards that a player has played
     *
     * @param p the player that has played
     * @param cards the cards played by {@param P}
     */
    public void printCardsPlayed(Player p, Card [] cards){
        int n = cards.length;

        System.out.print(p + " : has played - [ ");

        for(int i = 0; i < n; i ++){

            if(i < n - 1){
                System.out.print(cards[i] +", ");
            }else{
                System.out.print(cards[i]);
            }
        }
        System.out.println(" ]");

    }





    public static void main(String [] args){

        Game game = new Game();

        for(int j = 1; j < 4; j ++ ){
            game.addPlayer(new ComputerPlayer(j +" "));
        }

        game.start();
        System.out.println();
        System.out.println("Table " + game.table.size());
        System.out.println("Deck " + game.deck.getSize());

    }



}

