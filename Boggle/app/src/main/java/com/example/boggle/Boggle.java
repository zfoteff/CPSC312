/*
 * Boggle object class file with the functionality to implement a game of New Boggle
 * CPSC 312-01, Fall 2019
 * Programming Assignment #5
 *
 * @author Zac Foteff
 * @version v1.0 10/27/21
 */

package com.example.boggle;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Boggle {
    public static final int GRID_SIZE = 4;

    private HashMap<Character, Integer> wordMap;
    private static String[][] gameGrid;
    private List<String> usedWords;
    private StringBuilder currUserWord;
    private int points;
    private Context context;
    private List<String> correctWords;
    private static final String[] boggleDice = {
            "AAEEGN", "ABBJOO", "ACHOPS", "AFFKPS",
            "AOOTTW", "CIMOTU", "DEILRX", "DELRVY",
            "DISTTY", "EEGHNW", "EEINSU", "EHRTVW",
            "EIOSST", "ELRTTY", "HIQMNU", "HLNNRZ"
    };

    /**
     * Constructor for Boggle object instance
     *
     * @param context View Context for Boggle game
     */
    public Boggle(Context context) {
        this.wordMap = new HashMap<>();
        this.points = 0;
        this.context = context;
        this.currUserWord = new StringBuilder();
        this.usedWords = new ArrayList<>();
        loadWordsFromFile();
        initializeGameGrid();
    }

    /**
     * Initialize game board with randomly chosen letters from the list of boggle dice
     */
    private static void initializeGameGrid() {
        gameGrid = new String[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < 4 ; i ++) {
            for (int j = 0; j < 4; j++) {
                int randIdx = (int)(Math.random()*6);
                if (boggleDice[i+j].charAt(randIdx) == 'Q')
                    //  If the letter is a Q, add the u to it following proper game logic
                    gameGrid[i][j] = "Qu";
                else
                    gameGrid[i][j] = ""+boggleDice[i+j].charAt(randIdx);
            }
        }
    }

    /**
     * Re-roll boggle dice to make a new game board
     */
    public void regenerateGameGrid() {
        initializeGameGrid();
    }

    /**
     * Clear user map of letters, and delete user word
     */
    public void clearUserInput () {
        this.wordMap = new HashMap<>();
        currUserWord.delete(0, currUserWord.length());
    }

    /**
     * Add selected letter to the growing user word and map of letters
     *
     * @param letter Selected letter from letter grid
     */
    public void addLetterToUserWord(String letter) {
        this.currUserWord.append(letter);

        if (letter.charAt(0) == 'Q') {
            if (this.wordMap.containsKey(letter.charAt(1))) {
                this.wordMap.put(letter.charAt(1), this.wordMap.get(letter.charAt(0)) + 1);
            }
            else
                this.wordMap.put(letter.charAt(1), 1);
        }

        if (this.wordMap.containsKey(letter.charAt(0))) {
            this.wordMap.put(letter.charAt(0), this.wordMap.get(letter.charAt(0)) + 1);
        }
        else
            this.wordMap.put(letter.charAt(0), 1);
    }

    /**
     * Checks if a word is valid according to the New Boggle rules
     *
     * @return true is word is valid according to the New Boggle rules, false otherwise
     */
    public boolean isValidWord () {
        if (findInWordList(currUserWord.toString().toLowerCase()) && !isInPlayedWords(currUserWord.toString().toLowerCase()) && currUserWord.length() > 3) {
            //  If the word is valid, longer than the empty string, and it hasn't been played yet,
            //  then count up the points for the word and increment user points
            for (int each : wordMap.values()) {
                points += each;
            }

            usedWords.add(currUserWord.toString().toLowerCase());
            clearUserInput();
            return true;
        }

        return false;
    }

    /**
     * Check if user submitted string exists in the list of valid words
     *
     * @param inString user submitted string
     * @return true if String is in list of correct words, false otherwise
     */
    private boolean findInWordList(String inString) {
        for (String s : correctWords) {
            if (inString.equals(s))
                return true;
        }

        return false;
    }

    /**
     * Check if given string exists in the list of previously played words
     *
     * @param inString user inputted String
     * @return true if inString is in the list of played Strings, false otherwise
     */
    private boolean isInPlayedWords(String inString) {
        for (int i = 0; i < usedWords.size(); i ++) {
            if (inString.equals(usedWords.get(i)))
                return true;
        }

        return false;
    }

    /**
     *  Load words from words_alpha.txt file
     *  Credit: Dr. Gina Sprint
     */
    private void loadWordsFromFile() {
        correctWords = new ArrayList<>();
        try {
            InputStream in = context.getResources().openRawResource(R.raw.words_alpha);
            BufferedReader is = new BufferedReader(new InputStreamReader(in, "UTF8"));
            String line;
            do {
                line = is.readLine();
                correctWords.add(line);
            } while (line != null);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* Getters and Setters */
    public static String[] getBoggleDice() {
        return boggleDice;
    }

    public HashMap<Character, Integer> getWordMap() {
        return wordMap;
    }

    public void setWordMap(HashMap<Character, Integer> wordMap) {
        this.wordMap = wordMap;
    }

    public static String[][] getGameGrid() {
        return gameGrid;
    }

    public static void setGameGrid(String[][] gameGrid) {
        Boggle.gameGrid = gameGrid;
    }

    public List<String> getUsedWords() {
        return usedWords;
    }

    public void setUsedWords(List<String> usedWords) {
        this.usedWords = usedWords;
    }

    public StringBuilder getCurrUserWord() {
        return currUserWord;
    }

    public void setCurrUserWord(StringBuilder currUserWord) {
        this.currUserWord = currUserWord;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<String> getCorrectWords() {
        return correctWords;
    }

    public void setCorrectWords(List<String> correctWords) {
        this.correctWords = correctWords;
    }
}
