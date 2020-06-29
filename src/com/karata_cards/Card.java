package com.karata_cards;

/**
 * CARDS Value and rank
 *
 * Value => A    2   3   4   5   6   7   8   9   10  J   K   Q
 * Rank =>  1   2   3   4   5   6   7   8   9   10  11  12  13
 *
 *
 */

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

    /**
     * Get {@param cards} as a String
     * @param upto the number of cards to be printed
     * @param cards an array containing cards
     *
     * @return String representation of the cards
     */
    public static String revealCards(int upto, Card ... cards){


        String str = "[ ";
        for(int i = 0; i < upto; i ++){

            if(i < upto - 1){
                str += cards[i] +", ";
            }else{
                str += cards[i];
            }
        }


        return str +" ]";
    }



}
