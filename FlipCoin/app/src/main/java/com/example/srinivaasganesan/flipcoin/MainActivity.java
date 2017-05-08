package com.example.srinivaasganesan.flipcoin;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private static class MyHandler extends Handler {}
    private final MyHandler mHandler = new MyHandler();
    private MyRunnable mRunnable;
    private int color = 0;
    private View clickedTile = null;
    private View compTile = null;

    private int tile_color = 0;
    private int tile2_color = 0;
    private int[] occupied = {2,2};

    private final int user_color = 1; //User is white
    private final int comp_color = 1 - user_color;

    private boolean valid_click;
    private boolean user_turn = true;

    private int[][] board = new int[9][9];
    private HashSet<String> validMoves = new HashSet<String>();

    public static class MyRunnable implements Runnable {
        private final WeakReference<Activity> mActivity;
        private final int my_id;
        private final int my_color;

        public MyRunnable(Activity activity, int buttonid, int color) {
            mActivity = new WeakReference<>(activity);
            my_id = buttonid;
            my_color = color;
        }

        @Override
        public void run() {
            Activity activity = mActivity.get();
            if (activity != null) {
                ImageButton btn = (ImageButton) activity.findViewById(my_id);

                if(my_color==1)
                    btn.setImageResource(R.drawable.white);
                else
                    btn.setImageResource(R.drawable.black);

                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(btn, "rotationY", 91f, 180f);
                objectAnimator.setDuration(100L);
                objectAnimator.start();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btn = (ImageButton) findViewById(R.id.D4);
        btn.setImageResource(R.drawable.white);
        btn = (ImageButton) findViewById(R.id.E5);
        btn.setImageResource(R.drawable.white);
        btn = (ImageButton) findViewById(R.id.D5);
        btn.setImageResource(R.drawable.black);
        btn = (ImageButton) findViewById(R.id.E4);
        btn.setImageResource(R.drawable.black);

        ImageView temp;
        temp = (ImageView) findViewById(R.id.D4);
        temp.setClickable(false);
        temp = (ImageView) findViewById(R.id.D5);
        temp.setClickable(false);
        temp = (ImageView) findViewById(R.id.E4);
        temp.setClickable(false);
        temp = (ImageView) findViewById(R.id.E5);
        temp.setClickable(false);

        //init Board values
        for(int i=1; i<9; i++)
            for(int j=1; j<9; j++)
                board[i][j] = 2;

        board[4][4] = 1;
        board[4][5] = 0;
        board[5][4] = 0;
        board[5][5] = 1;

        validMoves.add("33");
        validMoves.add("34");
        validMoves.add("35");
        validMoves.add("36");

        validMoves.add("43");
        validMoves.add("46");
        validMoves.add("53");
        validMoves.add("56");

        validMoves.add("63");
        validMoves.add("64");
        validMoves.add("65");
        validMoves.add("66");
    }

    //FLIP A CELL
    protected void flip(View V, int color){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(V, "rotationY", 0f, 90f);
        objectAnimator.setDuration(100L);
        objectAnimator.start();

        mRunnable = new MyRunnable(this, V.getId(), color);
        mHandler.postDelayed(mRunnable, 100L);
    }

    //CLICK ON A CELL TO CHECK
    protected void checkValid(View V){

        if(user_turn == false)
            return;

        user_turn = false; //Block further clicks
        //Return previously clicked cell to original state
        String cell = V.getResources().getResourceName(V.getId());
        int len = cell.length();
        String cell_id = cell.substring(len-2, len);
        String cell_tile = cell_id + "_tile";

        if(clickedTile!=null) {
            if(tile_color == 0)
                clickedTile.setBackgroundResource(R.drawable.dark);
            else
                clickedTile.setBackgroundResource(R.drawable.light);
            clickedTile.setAlpha(0.8f);
        }

        //Set current cell to clicked state
        int tile_id = getResources().getIdentifier(cell_tile, "id", getPackageName());
        clickedTile = findViewById(tile_id);
        tile_color = getCellColor(cell_id);

        clickedTile.setAlpha(1f);
        if(tile_color == 0)
            clickedTile.setBackgroundResource(R.drawable.dark_border);
        else
            clickedTile.setBackgroundResource(R.drawable.light_border);

        //Check if click is valid
        int x_var = cell_id.charAt(0) - 64;
        int y_var = cell_id.charAt(1) - 48;

        String cellAsString = Integer.toString(x_var)+Integer.toString(y_var);

        if(board[x_var][y_var]!=2) {
            error_toast("TEST A");
            user_turn = true;
        }
        else{
            valid_click = false;
            int num_cells = 0;
            int[] x_val = {-1,0,1};
            int[] y_val = {-1,0,1};

            for(int i=0; i<3; i++)
                for(int j=0; j<3; j++)
                    num_cells += check_range(x_var, y_var, x_val[i] , y_val[j], user_color, true);

            if(valid_click == false) {
                user_turn = true;
                error_toast("");
            }
            else {
                occupied[user_color] += 1;
                addValidMoves(cellAsString);

                printBoard();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        computerMove();
                    }
                }, 2000);
            }
        }
    }

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

    //Given a cell add all valid moves
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
                        validMoves.add(Integer.toString(x_it) + Integer.toString(y_it));
                        Log.e("ADDED TO VALIDMOVE",Integer.toString(x_it) + Integer.toString(y_it));
                    }
                }

                //ADD CELLS TWO UNITS AWAY IN SAME SIDE
                if(x_t>0 && x_2t>0 && x_t<9 && x_2t<9 && y_t>0 && y_2t>0 && y_t<9 && y_2t<9){
                    if(board[x_t][y_t]!=2 && board[x_2t][y_2t]==2) {
                        validMoves.add(Integer.toString(x_2t) + Integer.toString(y_2t));
                        Log.e("ADDED TO VALIDMOVE",Integer.toString(x_2t) + Integer.toString(y_2t));
                    }
                }
            }
        }
    }

    protected void computerMove(){
        int x_var, y_var;
        Log.e("STEP","In Comp Move");

        int num_cells = 0;
        int[] x_val = {-1, 0, 1};
        int[] y_val = {-1, 0, 1};
        valid_click = false;

        int best_x = 1, best_y = 1, bestFlip = 0;

        //CHECK ALL MOVES FOR COMP
        for (String s : validMoves) {
            num_cells = 0;
            x_var = s.charAt(0) - 48;
            y_var = s.charAt(1) - 48;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    num_cells += check_range(x_var, y_var, x_val[i], y_val[j], comp_color, false);
                }
            }

            //UPDATE BEST COUNTS
            if(num_cells > bestFlip){
                bestFlip = num_cells;
                best_x = x_var;
                best_y = y_var;
            }
        }

        //THIS IS THE BEST TILE
        if (bestFlip > 0) {
            setCompTile(best_x, best_y);

            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    check_range(best_x, best_y, x_val[i], y_val[j], comp_color, true);

            occupied[comp_color] += 1;
        }

        Log.e("OCCUPIED", Integer.toString(occupied[0]) + " " + Integer.toString(occupied[1]));
        boolean userHasMove = userHasMoves(1);
        boolean compHasMove = (bestFlip>0);

        if(compHasMove == false){
            if(userHasMove == false)
                checkWin();
            else {
                Toast.makeText(this, "Comp has no moves, User gets another chance!", Toast.LENGTH_SHORT).show();
                user_turn = true;
            }
        }
        else{
            if(userHasMove == true)
                user_turn = true;
            else if(userHasMove == false){
                Toast.makeText(this, "User has no moves, Comp gets another chance!", Toast.LENGTH_SHORT).show();
                computerMove();
            }
        }
    }

    //Highlight Computer's selected Cell
    private void setCompTile(int x, int y){
        char row = (char) (x + 64);
        char col = (char) (y + 48);

        StringBuilder sb = new StringBuilder();
        sb.append(row);
        sb.append(col);
        String cell = sb.toString();
        String cell_tile = sb.append("_tile").toString();

        if(compTile!=null) {
            if(tile2_color == 0)
                compTile.setBackgroundResource(R.drawable.dark);
            else
                compTile.setBackgroundResource(R.drawable.light);
            compTile.setAlpha(0.8f);
        }

        //Set current cell to clicked state
        int tile_id = getResources().getIdentifier(cell_tile, "id", getPackageName());
        compTile = findViewById(tile_id);
        tile2_color = getCellColor(cell);

        compTile.setAlpha(1f);
        if(tile2_color == 0)
            compTile.setBackgroundResource(R.drawable.dark_tile);
        else
            compTile.setBackgroundResource(R.drawable.light_tile);
    }

    //Check if user has any moves left
    private boolean userHasMoves(int user){
        int x_var, y_var;
        int num_cells;
        int[] x_val = {-1,0,1};
        int[] y_val = {-1,0,1};

        for(String s : validMoves){
            x_var = s.charAt(0) - 48;
            y_var = s.charAt(1) - 48;

            for(int i=0; i<3; i++){
                for(int j=0; j<3; j++){
                    num_cells = check_range(x_var, y_var, x_val[i] , y_val[j], user, false);
                    if(num_cells > 0)
                        return true;
                }
            }
        }

        Log.e("USER","User has no moves");
        return false;
    }

    //CHECK RANGE
    protected int check_range(int x_start, int y_start, int x_incr, int y_incr, int curr_color, boolean mustSet){

        if(x_incr == 0 && y_incr == 0)
            return 0;

        int num_cells = 0;
        int target_color = 1 - curr_color;

        int x_var = x_start + x_incr;
        int y_var = y_start + y_incr;

        while(x_var > 0 && x_var < 9 && y_var > 0 && y_var < 9){
            //Seeing a cell of opposite color
            if(board[x_var][y_var] == target_color) {
                num_cells++;
            }
            //Seeing same color - either stop or report error
            else if(board[x_var][y_var] == curr_color && num_cells > 0){
                if(mustSet == true) {
                    valid_click = true;
                    //Change board
                    int x_t = x_start, y_t = y_start;
                    String cell = Integer.toString(x_start) + Integer.toString(y_start);
                    addValidMoves(cell);
                    validMoves.remove(cell);
                    Log.e("REMOVE_VALID_MOVE", cell);

                    while (x_t * x_incr <= x_var * x_incr && y_t * y_incr <= y_var * y_incr) {
                        //Log.e("COLOR_CHANGE", Integer.toString(x_t) + Integer.toString(y_t));
                        board[x_t][y_t] = curr_color;
                        x_t = x_t + x_incr;
                        y_t = y_t + y_incr;
                    }

                    flipRange(x_start, y_start, x_incr, y_incr, curr_color, num_cells);
                }
                else
                    return num_cells;

                break;
            }
            else {
                //CASE 2 & 3 & 4 - seeing blank cell at start or after few cells of target color
                break;
            }

            x_var = x_var + x_incr;
            y_var = y_var + y_incr;
        }

        return 0;
    }

    //CHECK WHO WON THE GAME
    private void checkWin(){
        Log.e("FINAL", Integer.toString(occupied[0]) + Integer.toString(occupied[1]));
        if(occupied[0] > occupied[1])
            Toast.makeText(this, "Comp Wins! Count: " + Integer.toString(occupied[0]), Toast.LENGTH_SHORT).show();
        else if(occupied[1] > occupied[0])
            Toast.makeText(this, "User Wins! Count: " + Integer.toString(occupied[1]), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Game ends in Draw!: ", Toast.LENGTH_SHORT).show();

        user_turn = false;
    }

    private void error_toast(String msg){
        Toast.makeText(this, msg+" Invalid Move", Toast.LENGTH_SHORT).show();
    }

    private void flipRange(int x_start, int y_start, int x_incr, int y_incr, int cell_color, int num_cells) {

        int x_t = x_start;
        int y_t = y_start;
        int x_end = x_start + (num_cells+1) * x_incr;
        int y_end = y_start + (num_cells+1) * y_incr; //Plus 1 to include to last cell

        occupied[cell_color] += num_cells;
        occupied[1 - cell_color] -= num_cells;

        char temp_x;
        char temp_y;

        StringBuilder sb;
        String current_cell;

        while(((x_t * x_incr) <= (x_end * x_incr)) && ((y_t * y_incr) <= (y_end * y_incr))){
            temp_x = (char) (x_t + 64);
            temp_y = (char) (y_t + 48);

            sb = new StringBuilder();
            sb.append(temp_x);
            sb.append(temp_y);
            current_cell = sb.toString();
            //Log.e("GOIN TO FLIP",current_cell);

            int resID = getResources().getIdentifier(current_cell, "id", getPackageName());
            flip(findViewById(resID), cell_color);

            x_t = x_t + x_incr;
            y_t = y_t + y_incr;
        }
    }

    //GET COLOR OF TILE BASED ON PARITY
    protected int getCellColor(String tile){
        if((tile.charAt(0)+tile.charAt(1))%2 == 0)
            return 1;
        else
            return 0;
    }
}
