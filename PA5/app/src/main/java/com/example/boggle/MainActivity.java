/*
 * Main activity for Android Boggle application
 * CPSC 312-01, Fall 2019
 * Programming Assignment #5
 *
 * @author Zac Foteff
 * @version v1.0 10/27/21
 */

package com.example.boggle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private static boolean isGamePlaying = false;
    Handler handler = null;
    int seconds = 30;
    int minutes = 1;

    Button[][] gridButtons = new Button[Boggle.GRID_SIZE][Boggle.GRID_SIZE];
    List<Button> validButtons = new ArrayList<>();
    Button playButton;
    Button clearButton;
    Button submitButton;
    Boggle game;
    TextView details;
    TextView timer;
    TextView points;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //  Timer tick logic
            tick();
            handler.postDelayed(this, 1000);

            if (minutes == 0 && seconds == 0)
                stopGame();
        }
    };

    /**
     * Enable all buttons and initialize gameboard with Boggle object values
     */
    private void startGame() {
        isGamePlaying = true;
        enableGameButtons();
        details.setText(R.string.welcome);

        if (handler == null) {
            handler = new Handler();
            handler.postDelayed(runnable, 1000);
        }

        clearButton.setEnabled(true);
        submitButton.setEnabled(true);
    }

    /**
     * Disable all buttons, display user score, and prompt user to play again
     */
    private void stopGame() {
        isGamePlaying = false;
        disableGameButtons();
        playButton.setEnabled(false);
        String text = getString(R.string.game_over, game.getPoints());
        details.setText(text);
        stopTimer();

        //  Reset
        submitButton.setText(R.string.restart);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });
    }

    /**
     * Re initialize the Boggle game board, and reset the view with the new values
     */
    private void restartGame() {
        isGamePlaying = true;
        game.regenerateGameGrid();
        game.setPoints(0);
        validButtons.clear();
        setGridButtonContent();
        enableGameButtons();
        playButton.setEnabled(true);
        resetSeconds();
        details.setText(R.string.welcome);

        String pointsText = getString(R.string.points, game.getPoints());
        points.setText(pointsText);

        if (handler == null) {
            handler = new Handler();
            handler.postDelayed(runnable, 1000);
        }

        //  Reset submit button listener
        submitButton.setText(R.string.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWord();
            }
        });
    }

    /**
     * Incriment one second of the timer
     */
    private void tick() {
        seconds -= 1;
        if (seconds < 0) {
            minutes -= 1;
            seconds = 59;
        }

        String secondsText = ""+seconds;
        if (seconds < 10)
            secondsText = "0"+seconds;

        String timerText = getString(R.string.timer_elem, ""+(minutes), secondsText);
        timer.setText(timerText);
    }

    /**
     * Stop the running timer
     */
    private void stopTimer () {
        //  To stop timer, remove runnable from the handlers scheduled queue
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }

    /**
     * Reset the timer's value
     */
    private void resetSeconds () {
        seconds = 30;
        minutes = 1;
        timer.setText(R.string.time);
    }

    /**
     * Submit the string the user has been building to be evaluated as a valid word by the Boggle
     * object
     */
    private void submitWord() {
        Log.d(TAG, "onClick: Submit button pressed");
        if (isGamePlaying && game.isValidWord()) {
            String pointsText = getString(R.string.points, game.getPoints());
            points.setText(pointsText);
            details.setText(R.string.correct);
        } else {
            details.setText(R.string.incorrect);
        }
        validButtons.clear();
        enableGameButtons();
        game.clearUserInput();
    }

    /**
     * Enable all grid buttons in the view
     */
    private void enableGameButtons() {
        for (Button[] row : this.gridButtons) {
            for (Button each : row) {
                each.setEnabled(true);
            }
        }
    }

    /**
     * Disable all grid buttons in the view
     */
    private void disableGameButtons() {
        for (Button[] row : this.gridButtons) {
            for (Button each : row) {
                each.setEnabled(false);
            }
        }
    }

    /**
     * Access the Boggle object's active letters and set the grid button text fields to the randomly
     * chosen letters
     */
    private void setGridButtonContent() {
        for (int i = 0; i < Boggle.GRID_SIZE; i++) {
            for (int j = 0; j < Boggle.GRID_SIZE; j++)
                gridButtons[i][j].setText(game.getGameGrid()[i][j]);
        }
    }

    /**
     * Find index of the clicked button
     *
     * @param center Button in the current context that has been clicked by the user
     * @return int[] with the x axis contained in the first index and the y axis contained in
     * the second
     */
    private int[] findButtonIndices(Button center) {
        int[] result = new int[2];
        for (int i = 0; i < Boggle.GRID_SIZE; i++) {
            for (int j = 0; j < Boggle.GRID_SIZE; j++) {
                if (gridButtons[i][j].equals(center)) {
                    result[0] = i;
                    result[1] = j;
                    return result;
                }
            }
        }

        return result;
    }

    /**
     * Populate list of adjacent buttons with the buttons surrounding the clicked button
     * @param center Button in the current context that has been clicked by the user
     */
    private void findAdjacentButtons(Button center) {
        //  Clear old adjacent list
        validButtons = new ArrayList<>();
        //  [row, column]
        int[] indices = findButtonIndices(center);

        //  Edge cases
        //  1. Top left
        //  2. Top right
        //  3. Bottom left
        //  4. Bottom right
        //  5. Top row
        //  6. Bottom row
        //  7. Right side
        //  8. Left side

        //  1.
        if (indices[0] == 0 && indices[1] == 0) {
            validButtons.add(gridButtons[0][1]);
            validButtons.add(gridButtons[1][0]);
            validButtons.add(gridButtons[1][1]);
            return;
        }

        //  2.
        if (indices[0] == 0 && indices[1] == 3) {
            validButtons.add(gridButtons[0][2]);
            validButtons.add(gridButtons[1][2]);
            validButtons.add(gridButtons[1][3]);
            return;
        }

        //  3.
        if (indices[0] == 3 && indices[1] == 0) {
            validButtons.add(gridButtons[2][0]);
            validButtons.add(gridButtons[2][1]);
            validButtons.add(gridButtons[3][1]);
            return;
        }

        //  4.
        if (indices[0] == 3 && indices[1] == 3) {
            validButtons.add(gridButtons[2][3]);
            validButtons.add(gridButtons[2][2]);
            validButtons.add(gridButtons[3][2]);
            return;
        }

        //  5.
        if (indices[0] == 0) {
            for (int i = 0; i < 2; i++) {
                validButtons.add(gridButtons[i][indices[1] - 1]);
                validButtons.add(gridButtons[i][indices[1]]);
                validButtons.add(gridButtons[i][indices[1] + 1]);
            }
            return;
        }

        //  6.
        if (indices[0] == 3) {
            for (int i = 3; i >= 1; i--) {
                validButtons.add(gridButtons[i][indices[1] - 1]);
                validButtons.add(gridButtons[i][indices[1]]);
                validButtons.add(gridButtons[i][indices[1] + 1]);
            }
            return;
        }

        if (indices[1] == 0) {
            for (int i = 0; i < 2; i++) {
                validButtons.add(gridButtons[indices[0] - 1][i]);
                validButtons.add(gridButtons[indices[0]][i]);
                validButtons.add(gridButtons[indices[0] + 1][i]);
            }
            return;
        }

        if (indices[1] == 3) {
            for (int i = 3; i >= 1; i--) {
                validButtons.add(gridButtons[indices[0] - 1][i]);
                validButtons.add(gridButtons[indices[0]][i]);
                validButtons.add(gridButtons[indices[0] + 1][i]);
            }
            return;
        }

        else {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++)
                    validButtons.add(gridButtons[indices[0] + i][indices[1] + j]);
            }
        }
    }

    /**
     * Event method containing the logic for playing a letter in a Boggle game by selecting a button
     * in the current view context
     *
     * @param v View element (button) that logic should be applied to
     */
    private void onGridButtonClick(View v) {
        Button clicked = (Button) v;

        if (validButtons.size() == 0) {
            //  If adjacent buttons empty --> first button, any button can be picked
            game.addLetterToUserWord(clicked.getText().toString());
            findAdjacentButtons(clicked);
            clicked.setEnabled(false);
            String text = getString(R.string.curr_word, game.getCurrUserWord().toString());
            details.setText(text);
        } else {
            //  Adjacent buttons exist for a already selected letter
            for (Button each : validButtons) {
                if (clicked.equals(each)) {
                    game.addLetterToUserWord(each.getText().toString());

                    //  Update details with the current word
                    String text = getString(R.string.curr_word, game.getCurrUserWord().toString());
                    details.setText(text);

                    //  regenerate adjacent buttons list
                    findAdjacentButtons(clicked);
                    clicked.setEnabled(false);
                    break;
                }
            }
        }

        Log.d(TAG, "onGridButtonClick: Grid Button");
    }

    /**
     * Event that is called on the creation of the app
     *
     * @param savedInstanceState Saved instance state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  Create game
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        game = new Boggle(getApplicationContext());

        details = findViewById(R.id.gameDetails);
        timer = findViewById(R.id.timerView);
        points = findViewById(R.id.pointsView);
        String text = getString(R.string.points, 0);
        points.setText(text);

        //  Game grid buttons
        gridButtons[0][0] = findViewById(R.id.b00);
        gridButtons[0][1] = findViewById(R.id.b01);
        gridButtons[0][2] = findViewById(R.id.b02);
        gridButtons[0][3] = findViewById(R.id.b03);
        gridButtons[1][0] = findViewById(R.id.b10);
        gridButtons[1][1] = findViewById(R.id.b11);
        gridButtons[1][2] = findViewById(R.id.b12);
        gridButtons[1][3] = findViewById(R.id.b13);
        gridButtons[2][0] = findViewById(R.id.b20);
        gridButtons[2][1] = findViewById(R.id.b21);
        gridButtons[2][2] = findViewById(R.id.b22);
        gridButtons[2][3] = findViewById(R.id.b23);
        gridButtons[3][0] = findViewById(R.id.b30);
        gridButtons[3][1] = findViewById(R.id.b31);
        gridButtons[3][2] = findViewById(R.id.b32);
        gridButtons[3][3] = findViewById(R.id.b33);
        setGridButtonContent();
        disableGameButtons();

        //  Game context buttons
        playButton = (Button) findViewById(R.id.playButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        submitButton = (Button) findViewById(R.id.submitButton);
        clearButton.setEnabled(false);
        submitButton.setEnabled(false);

        //  Set onclick listeners for each in the grid buttons list
        for (Button[] row : gridButtons) {
            for (Button each : row) {
                each.setOnClickListener(this::onGridButtonClick);
            }
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Play button pressed");
                if (!isGamePlaying) {
                    isGamePlaying = true;
                    startGame();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clear button pressed");
                if (isGamePlaying) {
                    game.clearUserInput();
                    validButtons.clear();
                    details.setText(R.string.empty_word);
                    enableGameButtons();
                }
            }
        });

        //  Reset
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Submit button pressed");
                if (isGamePlaying && game.isValidWord()) {
                    String pointsText = getString(R.string.points, game.getPoints());
                    points.setText(pointsText);
                    details.setText(R.string.correct);
                    enableGameButtons();
                } else {
                    details.setText(R.string.incorrect);
                    enableGameButtons();
                    validButtons.clear();
                }

                game.clearUserInput();
            }
        });
    }
}