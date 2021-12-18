package com.example.gradecalculator;

public class GradeCalculator {
    private static double min_avg, curr_avg, final_weight;

    public GradeCalculator() {
    }

    /**
     * Calculates final grade needed to reach desired grade
     * @return double of needed final grade
     */
    public static double calculateFinalGrade() {
        return (min_avg-curr_avg*(1-final_weight))/final_weight;
    }

    /*   Getters and Setters   */
    public static double getMinAvg() {
        return min_avg;
    }

    public static void setMinAvg(double min_avg) {
        GradeCalculator.min_avg = min_avg;
    }

    public static double getCurrAvg() {
        return curr_avg;
    }

    public static void setCurrAvg(double curr_avg) {
        GradeCalculator.curr_avg = curr_avg;
    }

    public static double getFinalWeight() {
        return final_weight;
    }

    public static void setFinalWeight(double final_weight) {
        GradeCalculator.final_weight = final_weight/100;
    }
}
