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

        boolean ok = true;
        while (ok) {
            String []  n = scan.next().split(" ");

            for(int i = 0; i < n.length; i ++){
                if(n[i].toLowerCase().equals("q")){
                    ok = false;
                }else{
                    try{
                        int y = Integer.parseInt(n[i]);
                        x.add(y);
                    }catch (IllegalArgumentException e){
                        System.out.println("Please insert numeric values & Q to submit" );
                        x.clear();
                        break;
                    }

                }
            }

        }

        int [] cards = new int[x.size()];

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
