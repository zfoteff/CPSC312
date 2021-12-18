package com.example.gradecalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText desiredGrade = findViewById(R.id.des_grade_choice);
        EditText minAvg = findViewById(R.id.min_avg_choice);
        EditText currAvg = findViewById(R.id.curr_avg_choice);
        EditText finalWeight = findViewById(R.id.final_weight_choice);
        Button submit = findViewById(R.id.submit);
        LinearLayout hiddenComponent = findViewById(R.id.result);
        TextView resultPrestring = findViewById(R.id.result_prestring);
        TextView resultAdvice = findViewById(R.id.result_advice);
        TextView result = findViewById(R.id.result_num);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (desiredGrade.getText().length()==0 || minAvg.getText().length()==0
                        ||currAvg.getText().length()==0 || finalWeight.getText().length()==0) {
                    Log.d(TAG,  "onClick: Empty fields, aborting...");

                } else {
                    String grade = desiredGrade.getText().toString().toUpperCase();
                    double curravg = Float.parseFloat(currAvg.getText().toString());
                    double weight =  Float.parseFloat(finalWeight.getText().toString());
                    double minavg = Float.parseFloat(minAvg.getText().toString());

                    GradeCalculator.setMinAvg(minavg);
                    GradeCalculator.setCurrAvg(curravg);
                    GradeCalculator.setFinalWeight(weight);
                    double finalGradeNum = GradeCalculator.calculateFinalGrade();
                    String finalGrade = String.format("%.2f", finalGradeNum);

                    if (finalGradeNum >= 95.0 && finalGradeNum <= 100.0)
                        resultAdvice.setText(getString(R.string.result_veryhigh));
                    if (finalGradeNum >= 80.0 && finalGradeNum < 95.0)
                        resultAdvice.setText(getString(R.string.result_high));
                    if (finalGradeNum >= 70.0 && finalGradeNum < 80.0)
                        resultAdvice.setText(getString(R.string.result_avg));
                    else
                        resultAdvice.setText(getString(R.string.result_low));


                    hiddenComponent.setVisibility(View.VISIBLE);
                    resultPrestring.setText(getString(R.string.result, grade));
                    result.setText(getString(R.string.result_percentage, finalGrade));

                    Log.d(TAG, "onClick: Final Grade: "+ finalGrade +" Min Avg.: " + minavg +" Curr. Avg.: "+ curravg +" Final Weight: "+ weight);
                }

                desiredGrade.setText("");
                minAvg.setText("");
                currAvg.setText("");
                finalWeight.setText("");
            }
        });
    }
}