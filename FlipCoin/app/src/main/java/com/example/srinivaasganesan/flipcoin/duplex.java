package com.example.srinivaasganesan.flipcoin;

import android.util.Log;

/**
 * Created by Srinivaas Ganesan on 5/10/2017.
 */

public class duplex {
    public GameBoard tBoard;
    public String tile;
    public int score;

    public duplex(GameBoard board, String s, int score){
        this.tBoard = board;
        this.tile = s;
        this.score = score;
    }
}
