package com.karata_cards;

import java.util.ArrayList;

import java.util.Scanner;

public class HumanPlayer extends Player {

    public Scanner scan = new Scanner(System.in);

    public HumanPlayer(String name) {
        super( name, 1);
    }

    @Override
    public Card place(Card c) {
        return null;
    }

    /**
     * Place the cards of the indicated indices
     * write 99 if you have completed your selection
     *
     * @param c
     * @param escape
     * @return
     */
    @Override
    public Card[] place(Card c, int escape) {
        ArrayList<Integer> x = new ArrayList<>();


        while (true) {
            int n = scan.nextInt();
            if(n != 99){
                x.add(n);
            }else{
                break;
            }

        }

        int[] cards = new int[x.size()];

        if (x.size() == 0) {
            return null;
        }

        int i = 0;
        for (Integer y : x) {
            if(y < count){
                cards[i] = y;
                i++;
            }
        }

        return place(cards, escape);
    }

}
