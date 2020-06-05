package com.karata_cards;

public class Card implements Comparable<Card> {

    public Suit suit;
    public String value;
    public int rank;

    public Card(String value, Suit suit, int rank){
        this.rank = rank;
        this.value = value;
        this.suit = suit;
    }

    public Card(Card c){
        this.value = c.value;
        this.rank = c.rank;
        this.suit = c.suit;
    }


    @Override
    public String toString(){
        return "["+ value + " of " + suit.toString().toLowerCase() +"]";
    }

    @Override
    public int compareTo(Card o) {
        return ((Integer)rank).compareTo(o.rank);
    }
}
