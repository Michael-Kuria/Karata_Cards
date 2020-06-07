package com.karata_cards;


import java.util.LinkedList;

public class ComputerPlayer extends Player {


    public ComputerPlayer(String name) {
        super(name);
    }

    /**
     * Place one or more card on the table
     * @param ca The card already on the table
     * @param escape an integer that indicates how we are to handle the c, in cases when the special cards have been placed i.e J,K,2,3
     * @return
     */
    @Override
    public Card[] place(Card ca, int escape){

        Card c = new Card(ca);
        int [] x = null;
        // in cases of when am supposed to react to the special cards
        if(escape == 1){
            // handle 2 or 3
            int a = -1;
            if(c.rank == 2 || c.rank == 3) {
                a = this.findByRank(c.rank);

                if (a == -1) { // if no 2 or 3 found return A
                    a = this.findByRank(1);
                }

            }else if(c.rank == 11){ // handle J
                a = this.findByRank(11);

            }else if(c.rank == 12){ // handle K
                a = this.findByRank(12);
            }

            //Game.getGame().escape = 0;
            if(a == -1){
                return null;
            }


            x = new int[]{a};

        }else{ // when am to treat the card as a normal Card

            LinkedList<Integer> list = new LinkedList<>();

            // based on the suit find if there is a  Q
            int b = findByRank(13);

            if(b != -1){
                for(int j = b ; j < count; j ++){
                    if((hand[j].suit == c.suit && hand[j].rank == 13) || (hand[j].rank == 13 && c.rank == 13)) {
                        list.addLast(j);
                        break;
                    }

                }

                // if no Q has been found find an 8

                if(list.isEmpty()){
                    b = findByRank(8);

                    if(b != -1){
                        for(int j = b; j < count; j ++){
                            if((hand[j].suit == c.suit && hand[j].rank == 8) || (hand[j].rank == 8 && c.rank == 8)) {
                                list.addLast(j);
                                break;
                            }
                        }
                    }
                }
            }


            // Check if either Q and 8 have been found

            if(!list.isEmpty()){
                c = new Card(hand[list.getLast()]);

                // add any remaining 8 or Q
                for(int i = count - 1; i >= 0; i -- ){
                    if(c.rank == hand[i].rank && !checkIfPicked(list,i)){
                        list.addLast(i);
                    }
                }

                c = new Card(hand[list.getLast()]);

                //find an answer based on the suit
                for(int i = count - 1; i >= 0; i -- ){
                    if(c.suit == hand[i].suit && c.rank != hand[i].rank ){
                        list.addLast(i);
                        break;
                    }
                }

            }else {

                // Find a card with similar Rank as ca
                b = findByRank(c.rank);

                if (b != -1) {
                    for (int i = b; i < count; i++) {
                        if (hand[i].rank == c.rank) {
                            list.addLast(i);
                        }
                    }

                } else {

                    // Find a card with similar Suit as ca
                    for (int i = count - 1; i >= 0; i--) {



                        if (c.suit == hand[i].suit) {
                            if(hand[i].rank == 11){
                                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");


                            }

                            list.addLast(i);
                            int k = i + 1;

                            for(;k < count; k ++){
                                if (hand[i].rank == hand[k].rank) {
                                    list.addLast(k);
                                }
                            }

                            int j = i - 1;

                            for (; j >= 0; j--) {
                                if (hand[i].rank == hand[j].rank) {
                                    list.addLast(j);
                                }
                            }
                            break;
                        }
                    }

                }

            }
            // if after searching Card has been found request a card using the available Ace
            if(list.isEmpty()){
                for(int i = 0; i < 4 && i < count; i ++){
                    if(hand[i].rank == 1){
                        list.addLast(i);
                        break;
                    }
                }
            }

            if(!list.isEmpty()){
                x = new int[list.size()];
                int j = 0;
                for(Integer i : list){

                    x[j] = i;
                    j ++;
                }

            }

        }
        if(x != null && x.length > 0){
            return place(x,escape);
        }
        return null;

    }


    /**
     * Avoid picking a card twice
     *
     * @param l list containing cards already picked
     * @param a the card to be picked
     * @return True if it's OK and False otherwise
     */
    public boolean checkIfPicked(LinkedList<Integer> l, int a){

        for(Integer i: l){
            if(a == i){
                return true;
            }
        }

        return false;
    }




    @Override
    public Card place(Card c) {
        sort();

       /* if(c.rank == 2 || c.rank == 3){
            int a = this.findByRank(c.rank);

            if(a == -1){
                a = this.findByRank(1);
                if(a == -1){
                    this.pick(c.rank);
                    return null;
                }
            }

            return place(a);
        }

        *//*if(c.rank == 11){
            int a = this.findByRank(c.rank);

            if(a == -1){
                return null;
            }

            return place(a);
        }*//*


        int a = findByRank(c.rank);

        if(a == -1){

            for(int i = 0; i < count; i ++){
                if(hand[i].suit == c.suit){
                    return  place(i);
                }
            }
        }else{
            return place(a);
        }


        pick();*/
        return null;
    }


}
