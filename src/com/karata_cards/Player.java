package com.karata_cards;


public abstract class Player {

    public Card [] hand; // cards at hand
    public String name;
    public int count = 0;
    public State state;


    Deck deck = Deck.getDeck();

    public Player(String name){
        this.name = name;
        hand = new Card[50];
        state = State.PlAYING;
    }


    /**
     * while picking only one card
     */
    public void pick(){
        if(state == State.CARDLESS){
            state = State.PlAYING;
        }

        hand[count] = deck.deal();
        System.out.println(this +" : has picked  " + hand[count]);
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
                move(arr,arr[i]);
                j ++;
            }
        }

        return cards;

    }


    /**
     * Keep the cards in-order
     * @param index
     */
    public void move(int [] arr, int index){

        for(int i = index; i < count - 1; i ++){
            hand[i] = hand[i + 1];

        }

        for(int i = index; i < arr.length; i ++){
            if(arr[i] > arr[index]){
                arr[i] --;
            }
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
            //System.out.println(1);
        }
        return ans;
    }

    public void updateState(){
        if(count == 0){
            state = State.WINNER;
        }

    }

    /**
     * Sort the cards in hand
     */
    public void sort(){

        System.out.print(this  +": Cards at hand " + count +" >>> ");
        printHand();
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

    /**
     * print the cards at hand
     */
    public void printHand(){
        System.out.print("{ ");
        for(int i = 0; i < count; i ++){
            System.out.print(hand[i]);
        }
        System.out.println(" }");

    }

    public abstract Card place(Card c);
    public abstract Card[] place(Card c,int escape);
    //public abstract void play(Card c);

    @Override
    public String toString(){
        return name;
    }
}
