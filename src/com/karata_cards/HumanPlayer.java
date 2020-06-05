package com.karata_cards;

public class HumanPlayer extends Player {


    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public Card place(Card c) {
        return null;
    }

    @Override
    public Card[] place(Card c, int escape) {
        return new Card[0];
    }

}
