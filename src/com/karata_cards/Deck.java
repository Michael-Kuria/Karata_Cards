package com.karata_cards;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Handles creation of a deck, shuffling the cards and dealing cards to the player
 */
public class Deck {

    public Queue<Card> cards;

    public static Deck deck = null;


    private Deck(){
        cards = new ArrayDeque<>();
        Suit [] suits = Suit.values();
        String [] values = new String[] {"A","2","3","4","5","6","7","8","9","10","J","K","Q"};

        /**
         * Initializing the deck with all the cards excluding JOKERS
         */
        for(int i = 0; i < values.length; i ++){

            for(int j = 0; j < 4; j ++){

                cards.offer(new Card(values[i],suits[j],i + 1));
            }

        }

        // shuffle the deck of cards
        shuffle();

    }

    /**
     * Singleton class only way to access the deck is by this method;
     * @return
     */
    public static Deck getDeck(){
        if(deck == null){

            deck = new Deck();
        }
        return deck;
    }


    /**
     * Give a player a card
     * @return the card on top
     */
    public Card deal(){
        if(cards.isEmpty()){
            Game.getGame().updateDeck();
        }

        return cards.poll();
    }

    /**
     * Shuffle the deck of card so that it is in a random order
     */
    public void shuffle(){
        Card [] arr = new Card[52];

        int i = 0;
        for(Card c: cards){
            arr[i] = cards.poll();
            i ++;
        }


        for(int j = 0; j < 1000; j ++){

            int a = (int)(Math.random() * i);
            int b = (int)(Math.random() * i);

            // swap cards[a] and cards[b] if not equal

            if(a != b){
                Card temp = new Card(arr[a]);
                arr[a] = arr[b];
                arr[b] = temp;
            }

        }


        for(int j = 0; j < i; j ++){

            /*if(arr[j] == null){
                System.out.println(j +" is null during shuffling >>>>>>>>>>>>>>");
            }*/
            cards.offer(arr[j]);
        }
    }

    /**
     * Get the size of the available cards on the deck
     *
     * @return size of deck
     */
    public int getSize(){

        return cards.size();
    }

    //Testing the Deck class
    /*public static void main(String [] args){

        Deck deck = Deck.getDeck();

        for(Card c : deck.cards){
            System.out.println(c);

        }

        System.out.println(">>>>>>>>>>><<<<<<<<<<<");
        for(int i = 0; i < 10; i ++){
            System.out.println(deck.deal());
        }


    }*/
}
