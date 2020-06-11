package com.karata_cards;


public abstract class Player {

    public Card [] hand; // cards at hand
    public String name;
    public int count;
    public State state;


    Deck deck = Deck.getDeck();

    public Player(String name){
        this.name = name;
        hand = new Card[50];
        state = State.PlAYING;
        count = 0;
    }

    public Player(Player p){
        this(p.name);
    }


    /**
     * while picking only one card
     */
    public void pick(){
        if(state == State.CARDLESS){
            state = State.PlAYING;
        }

        hand[count] = deck.deal();
        count ++;
        sort();
    }


    /**
     * while picking several cards
     * @param a the number of cards to pick
     */

    public void pick(int a){
        if(state == State.CARDLESS){
            state = State.PlAYING;
        }

        for(int i = 0; i < a; i ++){
            hand[count] = deck.deal();
            count ++;
        }
        sort();
    }

    /**
     * Place a card on the
     * @return
     */
    public Card place(int index){

        Card card = null;
        if(index < count){
            card = new Card(hand[index]);
            //move(index);
            count --;

        }

        return card;
    }

    /**
     * Place More than one card
     * @param arr the indices of the cards to be placed
     * @param escape
     * @return the List of cards
     */
    public Card[] place(int [] arr, int escape){

        Card [] cards = new Card[arr.length];

        int j = 0;
        for(int i = 0; i < arr.length; i ++){

            if(arr[i] < count){
                cards[j] = new Card(hand[arr[i]]);
                move(arr,i);
                j ++;
            }
        }

        return cards;

    }


    /**
     * If a card is to be placed in the table ensure that it is removed from the arr
     *
     * @param arr The array consisting the all indices of the hand that are to be placed on the table
     * @param index The index that needs to be removed, in arr.
     */
    public void move(int [] arr, int index){

        for(int i = index; i < arr.length; i ++){
            if(arr[i] > arr[index]){
                arr[i] --;
            }
        }

        for(int i = arr[index]; i < count - 1; i ++){
            hand[i] = hand[i + 1];

        }



        hand[count --] = null;
    }

    /**
     * Find a card based on it rank. Find the leftmost card of this rank
     * @param i
     * @return
     */
    public int findByRank(int i){

        int l = 0, h = count - 1;

        int ans = -1;
        while(l <= h){
            int mid = (l + h)/ 2;

            if(hand[mid].rank == i){
                ans = mid;
                h = mid - 1;
            }else if(hand[mid].rank < i){
                l = mid + 1;
            }else{
                h = mid - 1;
            }

        }
        return ans;
    }

    /***
     * This function will be called when the player has violated the rules of the game
     * all the cards that he has placed on the table will be returned to him
     * @param cards cards placed on the table
     */
    public void addToHand(Card [] cards){
        for(Card c : cards){
            hand[count] = c;
            count ++;
        }
    }


    /**
     * Sort the cards at hand
     */
    public void sort(){

        for(int i = 0; i < count; i ++){

            for(int j = count - 1; j > i; j --){

                if(hand[j].rank < hand[j - 1].rank){
                    Card temp = hand[j];
                    hand[j] = hand[j - 1];
                    hand[j - 1] = temp;
                }
            }
        }
    }


    public abstract Card place(Card c);
    public abstract Card[] place(Card c,int escape);

    @Override
    public String toString(){
        return name;
    }
}
