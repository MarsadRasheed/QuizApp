package com.hamlet.quizapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_QUIZ = 1;
    public static final String SHARED_PREF = "shared_pref";
    public static final String SHARED_HIGHSCORE = "shared_high_score";
    public static final String DIFFICULTY = "difficulty";
    public static final String CATEGORY_ID = "categoryId";
    public static final String CATEGORY_NAME = "categoryName";

    private TextView titleText;
    private TextView highScoreText;
    private Button startButton;
    private Spinner spinner;
    private Spinner categorySpinner;
    private int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleText = findViewById(R.id.titleText);
        highScoreText = findViewById(R.id.highscoreText);
        startButton = findViewById(R.id.startButton);
        spinner = findViewById(R.id.spinner);
        categorySpinner = findViewById(R.id.spinnerCategories);
//
//        String[] difficult = Question.getAllDifficultyLevels();
//        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,difficult);
//        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapterDifficulty);

        loadDifficulty();
        loadCategory();

        loadSharedPref();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String difficulty = spinner.getSelectedItem().toString();
                Category category = (Category) categorySpinner.getSelectedItem();
                int categoryId = category.getId();
                String categoryName = category.getName();
                Intent intent = new Intent(MainActivity.this,QuizActivity.class);
                intent.putExtra(DIFFICULTY,difficulty);
                intent.putExtra(CATEGORY_ID,categoryId);
                intent.putExtra(CATEGORY_NAME,categoryName);
                startActivityForResult(intent,REQUEST_CODE_QUIZ);
            }
        });
    }

    public void loadCategory() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        List<Category> categoryList = databaseHelper.getAllCategories();
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    public void loadDifficulty() {
        String[] difficult = Question.getAllDifficultyLevels();
        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,difficult);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterDifficulty);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_QUIZ && resultCode == RESULT_OK){
            highScore = data.getIntExtra("HIGHSCORE",0);
            highScoreText.setText("HighScore : " + highScore);
            setSharedEditor();
        }
    }

    public void setSharedEditor(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_HIGHSCORE,highScore);
        editor.apply();
    }

    public void loadSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        highScore = sharedPreferences.getInt(SHARED_HIGHSCORE,highScore);
        highScoreText.setText("HighScore : " + highScore);
    }

}
