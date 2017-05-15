package com.example.srinivaasganesan.flipcoin;

import android.util.Log;

import java.util.HashSet;

/**
 * Created by Srinivaas Ganesan on 5/10/2017.
 */

public class GameBoard {

    private int[][] board = new int[9][9];
    private HashSet<String> moves = new HashSet<String>();
    private int color;

    //CREATE A GAME BOARD
    public GameBoard(int[][] inBoard, HashSet<String> inMoves, int color){
        for(int i=1; i<9; i++)
            for(int j=1; j<9; j++)
                board[i][j] = inBoard[i][j];

        moves.addAll(inMoves);
        this.color = color;
    }

    /*
    protected String getBestMove(){
        int score = 0;
        int oppScore = 0;
        int temp = 0;
        int bestscore = -1000;

        String bestCell = null; //If BestCell is null, means no valid moves

        int x_incr[] = {-1,0,1};
        int y_incr[] = {-1,0,1};
        duplex db = null;
        StringBuilder sb = new StringBuilder();

        for(String s : moves) {
            sb.append(s);
            sb.append(" ");
        }

        //Log.e("DECISION MAKING ", sb.toString());

        for(String s : moves){
            score = 0;
            oppScore = 0;

            for(int i=0; i<3; i++){
                for(int j=0; j<3; j++){
                    db = checkMove(s, x_incr[i], y_incr[j]);
                    if(db != null) {
                        score += db.score;
                        //db.tBoard.printBoard();
                        temp = db.tBoard.bestOppScore();
                        oppScore = oppScore > temp ? oppScore : temp;
                    }
                }
            }

            //TOTAL FOR THIS ONE MOVE
            //Log.e("SCORE ", s + " " + Integer.toString(score));
            //Log.e("OPP SCORE ", s + " " + Integer.toString(oppScore));

            if(score > 0){
                //s is a valid move
                score = score - oppScore;
                if(score > bestscore) {
                    bestCell = s;
                    bestscore = score;
                }
            }
        }
        return bestCell;
    }

    protected int getBestScore(){
        int score = 0;
        int oppScore = 0;
        int temp = 0;
        int bestscore = -1000;

        String bestCell = null; //If BestCell is null, means no valid moves

        int x_incr[] = {-1,0,1};
        int y_incr[] = {-1,0,1};
        duplex db = null;
        StringBuilder sb = new StringBuilder();

        for(String s : moves) {
            sb.append(s);
            sb.append(" ");
        }

        //Log.e("DECISION MAKING ", sb.toString());

        for(String s : moves){
            score = 0;
            oppScore = 0;

            for(int i=0; i<3; i++){
                for(int j=0; j<3; j++){
                    db = checkMove(s, x_incr[i], y_incr[j]);
                    if(db != null) {
                        score += db.score;
                        //db.tBoard.printBoard();
                        temp = db.tBoard.bestOppScore();
                        oppScore = oppScore > temp ? oppScore : temp;
                    }
                }
            }

            if(score > 0){
                //s is a valid move
                score = score - oppScore;
                if(score > bestscore) {
                    bestCell = s;
                    bestscore = score;
                }
            }
        }
        return bestscore;
    }

    protected String getBestMove2(){
        int score = 0;
        int oppScore = 0;
        int bestscore = -1000, t_bestscore = -1000;
        int t_score, t_oppScore;

        String bestCell = null; //If BestCell is null, means no valid moves
        String t_bestCell = null;
        duplex db, t_db, oppDb;

        for(String s : moves) {
            score = 0;
            oppScore = 0;
            t_bestscore = -1000;

            //CHECK IF S IS A VALID MOVE FOR BLACK
            db = checkMove2(s);
            if (db != null) {
                //IF VALID - SAVE SCORE FOR BLACK AND WHITE
                score = db.score;
                Log.e("L1",s+" "+Integer.toString(score));
                oppDb = db.tBoard.bestOppBoard();

                if (oppDb != null) {
                    oppScore = oppDb.score;
                    Log.e("L1 OPP",s+" "+Integer.toString(oppScore));

                    //OF THE RESULTING BOARD - GET ALL MOVES
                    for (String s1 : oppDb.tBoard.moves) {
                        t_score = 0;
                        t_oppScore = 0;

                        //CHECK IF THIS IS A VALID MOVE FOR BLACK
                        t_db = checkMove2(s1);
                        if (t_db != null) {
                            t_score = t_db.score;
                            t_oppScore = t_db.tBoard.bestOppScore();
                            Log.e("L2 ",s1+" "+Integer.toString(t_score));
                            Log.e("L2 OPP",s1+" "+Integer.toString(t_oppScore));

                            if (t_score > 0) {
                                t_score = t_score - t_oppScore;
                                if (t_score > t_bestscore) {
                                    t_bestCell = s1;
                                    t_bestscore = t_score;
                                }
                            }
                        }
                    }

                    Log.e("L2 BEST",s+" "+Integer.toString(t_bestscore));

                    if (score > 0) {
                        if (t_bestscore > -1000)
                            score += t_bestscore;

                        score = score - oppScore;
                        Log.e("L2 NET ",s+" "+Integer.toString(score));
                        Log.e("","");

                        if (score > bestscore) {
                            bestCell = s;
                            bestscore = score;
                        }
                    }
                }
            }
        }

        return bestCell;
    }

    //PRINT THE BOARD TO LOG
    private void printBoard(){
        StringBuilder sb = new StringBuilder();
        for(int i=1; i<9; i++){
            for(int j=1; j<9; j++){
                sb.append(Integer.toString(board[i][j])+" ");
            }
            Log.e("BOARD",sb.toString());
            sb = new StringBuilder();
        }
    }

    //RETURNS THE BOARD FROM THE BEST MOVE THE OPPONENT CAN MAKE
    private duplex bestOppBoard(){
        int score = 0, oppScore = 0;
        int bestScore = 0;
        duplex bestDB = null;

        for(String s : moves){
            score = 0;

            duplex db = checkMove2(s);
            if(db != null) {
                score = db.score;
                oppScore = db.tBoard.getBestScore();

                if(score > 0) {
                    score = score - oppScore;
                    if(score > bestScore) {
                        bestScore = score;
                        bestDB = db;
                    }
                }
            }
        }
        return bestDB;
    }

    //CHECK IS A MOVE IS VALID FOR A PARTICULAR COLOR
    private duplex checkMove(String s, int x_incr, int y_incr) {
        if(x_incr == 0 && y_incr == 0)
            return null;

        //Log.e("CHECKMOVE", s+" "+Integer.toString(color)+" "+Integer.toString(x_incr)+" "+Integer.toString(y_incr));

        int x_start = (s.charAt(0) - 48);
        int y_start = (s.charAt(1) - 48);
        duplex db = null;
        int score = 0;

        int num_cells = 0;
        int target_color = 1 - color;

        int x_var = x_start + x_incr;
        int y_var = y_start + y_incr;

        while (x_var > 0 && x_var < 9 && y_var > 0 && y_var < 9) {
            //Seeing a cell of opposite color
            if (board[x_var][y_var] == target_color) {
                num_cells++;
            }
            //Seeing same color - either stop or report error
            else if (board[x_var][y_var] == color && num_cells > 0) {
                int x_t = x_start, y_t = y_start;
                String cell = Integer.toString(x_start) + Integer.toString(y_start);

                //CHANGE BOARD
                GameBoard gb = new GameBoard(board, moves, 1-color);
                gb.addValidMoves(cell);
                gb.moves.remove(cell);
                score = num_cells;

                //CHECK IF CAPTURE IS CORNER
                if(cell.compareTo("11")==0 || cell.compareTo("18")==0 || cell.compareTo("81")==0 || cell.compareTo("88")==0)
                    score += 100;
                if(x_start == 1 || x_start == 8 || y_start == 1 || y_start == 8)
                    score += 10;

                while (x_t * x_incr <= x_var * x_incr && y_t * y_incr <= y_var * y_incr) {
                    gb.board[x_t][y_t] = color;
                    x_t = x_t + x_incr;
                    y_t = y_t + y_incr;
                }
                Log.e("DUPLEX INIT FOR ", s + Integer.toString(gb.color));
                db = new duplex(gb, score);
                break;
            } else {
                break;
            }

            x_var = x_var + x_incr;
            y_var = y_var + y_incr;
        }
        return db;
    }
    */

    //CHECK IS A MOVE IS VALID FOR A PARTICULAR COLOR
    private duplex checkMove2(String s) {

        int x_start = (s.charAt(0) - 48);
        int y_start = (s.charAt(1) - 48);

        duplex db = null;
        GameBoard gb;
        int score = 0;

        int x_arr[] = {-1,0,1};
        int y_arr[] = {-1,0,1};
        int x_var, y_var;
        int x_incr, y_incr;

        int num_cells = 0;
        int target_color = 1 - color;

        for(int i=0; i<3; i++) {
            for (int j = 0; j < 3; j++) {

                x_incr = x_arr[i];
                y_incr = y_arr[j];

                num_cells = 0;
                if(x_incr==0 && y_incr==0)
                    continue;

                x_var = x_start + x_incr;
                y_var = y_start + y_incr;

                while (x_var > 0 && x_var < 9 && y_var > 0 && y_var < 9) {
                    //Seeing a cell of opposite color
                    if (board[x_var][y_var] == target_color) {
                        num_cells++;
                    }
                    //Seeing same color - either stop or report error
                    else if (board[x_var][y_var] == color && num_cells > 0) {
                        int x_t = x_start, y_t = y_start;

                        //IF A BOARD EXISTS USE IT, ELSE INIT A NEW BOARD
                        if(db!=null)
                            gb = db.tBoard;
                        else
                            gb = new GameBoard(board, moves, 1 - color);

                        score += num_cells;

                        while (x_t * x_incr <= x_var * x_incr && y_t * y_incr <= y_var * y_incr) {
                            gb.board[x_t][y_t] = color;
                            x_t = x_t + x_incr;
                            y_t = y_t + y_incr;
                        }

                        //Log.e("DUPLEX INIT FOR ", s + Integer.toString(gb.color));
                        if(db==null)
                            db = new duplex(gb, s, score);
                        else
                            db.score = score;

                        break;
                    } else {
                        break;
                    }

                    x_var = x_var + x_incr;
                    y_var = y_var + y_incr;
                }
            }
        }

        //CHECK IF CAPTURE IS CORNER
        if(db!=null) {
            if (s.compareTo("11") == 0 || s.compareTo("18") == 0 || s.compareTo("81") == 0 || s.compareTo("88") == 0)
                db.score += 100;
            if (x_start == 1 || x_start == 8 || y_start == 1 || y_start == 8)
                db.score += 10;
            db.tBoard.addValidMoves(s);
            db.tBoard.moves.remove(s);
        }

        return db;
    }

    protected void addValidMoves(String cellAsString){
        int x = cellAsString.charAt(0) - 48;
        int y = cellAsString.charAt(1) - 48;

        int x_t, y_t, x_it, y_it, x_2t, y_2t;
        int[] step = {-1,0,1};

        //ADD CELLS ONE UNIT AWAY
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                x_t = x + step[i];
                y_t = y + step[j];

                x_it = x + -1*step[i];
                y_it = y + -1*step[j];

                x_2t = x + 2*step[i];
                y_2t = y + 2*step[j];

                //ADD CELLS IN OPPOSITE SIDE
                if(x_t>0 && x_it>0 && x_t<9 && x_it<9 && y_t>0 && y_it>0 && y_t<9 && y_it<9){
                    if(board[x_t][y_t]!=2 && board[x_it][y_it]==2) {
                        moves.add(Integer.toString(x_it) + Integer.toString(y_it));
                        //Log.e("VALID_MOVES : " ,Integer.toString(x_it) + Integer.toString(y_it));
                    }
                }

                //ADD CELLS TWO UNITS AWAY IN SAME SIDE
                if(x_t>0 && x_2t>0 && x_t<9 && x_2t<9 && y_t>0 && y_2t>0 && y_t<9 && y_2t<9){
                    if(board[x_t][y_t]!=2 && board[x_2t][y_2t]==2) {
                        moves.add(Integer.toString(x_2t) + Integer.toString(y_2t));
                        //Log.e("VALID_MOVES", Integer.toString(x_2t) + Integer.toString(y_2t));
                    }
                }
            }
        }
    }

    protected duplex getBest(int num){
        int score = 0;
        int oppScore = 0;
        int bestscore = -10000;
        String spaces = " ";

        if(num == 0)
            spaces = "          ";
        else if(num == 1)
            spaces = "      ";

        duplex oppDB = null;
        duplex bestDB = null;
        duplex db = null;

        int x_incr[] = {-1,0,1};
        int y_incr[] = {-1,0,1};

        for(String s : moves){
            score = 0;
            oppScore = 0;

            //CHECK IF S IS A VALID MOVE FOR BLACK
            db = checkMove2(s);
            if(db != null) {

                String color = "WHITE";
                if(db.tBoard.color == 1)
                    color = "BLACK";
                //Log.e("INBTWN ", spaces + " " + color+" "+Integer.toString(num) + " " + db.tile + " " + Integer.toString(db.score));

                //s is a valid move
                score = db.score;

                if(num > 0) {
                    oppDB = db.tBoard.getBest(num-1);
                    if(oppDB != null)
                        oppScore = oppScore > oppDB.score ? oppScore : oppDB.score;
                }

                score = score - oppScore;
                if(score > bestscore){
                    bestscore = score;
                    bestDB = db;
                    bestDB.score = score;
                }
            }
        }

        if(bestDB!=null) {
            String color = "WHITE";
            if(bestDB.tBoard.color == 1)
                color = "BLACK";
            //Log.e("BEST ", spaces + " " + color+" "+Integer.toString(num) + " " + bestDB.tile + " " + Integer.toString(bestDB.score));
        }

        return bestDB;
    }
}
