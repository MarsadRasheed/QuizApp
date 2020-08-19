package com.hamlet.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import static com.hamlet.quizapp.MainActivity.CATEGORY_ID;
import static com.hamlet.quizapp.MainActivity.CATEGORY_NAME;
import static com.hamlet.quizapp.MainActivity.DIFFICULTY;

public class QuizActivity extends AppCompatActivity {

    public static final long COUNTDOWN_MILIES = 31000;

    private TextView scoreText;
    private TextView remainingText;
    private TextView countDownText;
    private TextView questionText;
    private TextView difficultyText;
    private TextView categoryText;

    private RadioGroup radioGroup;
    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;

    private Button confirmButton;
    private List<Question> questionList;

    private int questionCounter;
    private int totalQuestion;
    private ColorStateList colorStateList;
    private ColorStateList colorStateListForCounter;
    private boolean answered;
    private int score;
    public  int highScore = 0;
    private Question currentQuestion;

    private long backPressedTime;

    private long leftCountdown;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        scoreText = findViewById(R.id.scoreText);
        remainingText = findViewById(R.id.remainText);
        countDownText = findViewById(R.id.countdownText);
        questionText = findViewById(R.id.questionText);
        difficultyText = findViewById(R.id.difficultyText);
        categoryText = findViewById(R.id.categoryText);

        radioGroup = findViewById(R.id.radioGroup);
        option1 = findViewById(R.id.radioButton);
        option2 = findViewById(R.id.radioButton2);
        option3 = findViewById(R.id.radioButton3);

        confirmButton = findViewById(R.id.confirmButton);
        String difficulty = getIntent().getStringExtra(DIFFICULTY);
        String categoryName = getIntent().getStringExtra(CATEGORY_NAME);
        int categoryId = getIntent().getIntExtra(CATEGORY_ID,0);

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        questionList = databaseHelper.getQuestions(categoryId,difficulty);

        colorStateList = option1.getTextColors();
        colorStateListForCounter = countDownText.getTextColors();
        difficultyText.setText("Difficulty : " + difficulty);
        categoryText.setText("Category : " + categoryName);
        totalQuestion = questionList.size();
        Collections.shuffle(questionList);


        if(!questionList.isEmpty()){
            showNextQuestion();
        } else {
            Toast.makeText(this, "We've not questions of " + categoryName + " having difficulty " + difficulty, Toast.LENGTH_LONG).show();
            finish();
        }


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!answered){
                    if(isChecked()){
                        checkAnswer();
                    } else{
                        Toast.makeText(QuizActivity.this, "Please, select an option!", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    showNextQuestion();
                }
            }
        });
    }

    public void checkAnswer(){
        RadioButton selected = findViewById(radioGroup.getCheckedRadioButtonId());
        countDownTimer.cancel();
        int answer = radioGroup.indexOfChild(selected) + 1;

        if(answer == currentQuestion.getAnswer()){
            score++;
            setHighScore();
            scoreText.setText("Score : " + score);
        }
        showSolution();
    }

    public void setHighScore(){
        if(score > highScore){
            highScore = score;
        }
    }

    public boolean isChecked(){
        if ( option1.isChecked() || option2.isChecked() || option3.isChecked()){
            return true;
        } else{
            return false;
        }
    }

    public void showSolution(){
        answered = true;
        option1.setTextColor(Color.RED);
        option2.setTextColor(Color.RED);
        option3.setTextColor(Color.RED);

        if(!isChecked()){
            Toast.makeText(this, "Please, select an option!", Toast.LENGTH_SHORT).show();
        }

        switch (currentQuestion.getAnswer()){
            case 1:
                option1.setTextColor(Color.GREEN);
                questionText.setText("Answer A is correct");
                break;
            case 2:
                option2.setTextColor(Color.GREEN);
                questionText.setText("Answer B is correct");
                break;
            case 3:
                option3.setTextColor(Color.GREEN);
                questionText.setText("Answer C is correct");
                break;
        }
        if(questionCounter < totalQuestion){
            confirmButton.setText("Next");
        } else {
            confirmButton.setText("Finish");
        }
    }

    public void showNextQuestion(){
        radioGroup.clearCheck();

        option1.setTextColor(colorStateList);
        option2.setTextColor(colorStateList);
        option3.setTextColor(colorStateList);

        if( questionCounter < totalQuestion) {
            currentQuestion = questionList.get(questionCounter);

            questionText.setText(currentQuestion.getQuestion());
            option1.setText(currentQuestion.getOption1());
            option2.setText(currentQuestion.getOption2());
            option3.setText(currentQuestion.getOption3());
            questionCounter++;

            remainingText.setText("Question : " + questionCounter + " / " + totalQuestion);
            confirmButton.setText("Confirm");
            answered = false;
            leftCountdown = COUNTDOWN_MILIES;
            startCountDown();

        } else{
            finishQuiz();
        }
    }

    public void startCountDown(){
        countDownTimer = new CountDownTimer(leftCountdown,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                leftCountdown = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                leftCountdown = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (leftCountdown / 1000) / 60;
        int seconds = (int) (leftCountdown / 1000) % 60;
        String format = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        countDownText.setText(format);

        if(leftCountdown < 10000){
            countDownText.setTextColor(Color.RED);
        } else {
            countDownText.setTextColor(colorStateListForCounter);
        }

    }

    public void finishQuiz(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("HIGHSCORE",highScore);
        setResult(RESULT_OK,resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if( backPressedTime + 2000 > System.currentTimeMillis()){
            finishQuiz();
        } else {
            Toast.makeText(this, "Press Back again to Finish!", Toast.LENGTH_SHORT).show();
        }
         backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

}
