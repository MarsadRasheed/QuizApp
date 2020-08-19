package com.hamlet.quizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quiz.db";
    private static int  version = 9;
    private SQLiteDatabase db;

    private static DatabaseHelper instance;

    private DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    public static synchronized DatabaseHelper getInstance(Context context){
        if(instance == null){
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String createCatTable = "Create table " + QuizContract.Category.TABLE_NAME + " ( "
                + QuizContract.Category._ID + " integer Primary key autoincrement , "
                + QuizContract.Category.NAME_COLUMN + " TEXT ) ";

        final String createTable = "Create table " + QuizContract.QuestionTable.TABLE_NAME + " ( "
                + QuizContract.QuestionTable._ID + " integer primary key autoincrement , "
                + QuizContract.QuestionTable.QUESTION_COLUMN + " TEXT , "
                + QuizContract.QuestionTable.OPTION1_COLUMN+ " TEXT , "
                + QuizContract.QuestionTable.OPTION2_COLUMN + " TEXT , "
                + QuizContract.QuestionTable.OPTION3_COLUMN + " TEXT , "
                + QuizContract.QuestionTable.ANSWER_COLUMN + " integer , "
                + QuizContract.QuestionTable.DIFFICULTY_COULUM + " TEXT , "
                + QuizContract.QuestionTable.CATEGORY_COLUMN+ " integer , Foreign key ( "
                + QuizContract.QuestionTable.CATEGORY_COLUMN + " ) References " + QuizContract.Category.TABLE_NAME
                + " ( " + QuizContract.Category._ID + " ) " + " On delete CASCADE "
                + " ) ";

        db.execSQL(createCatTable);
        db.execSQL(createTable);
        fillCategoryTable();
        fillQuestionsTable();
    }

    public void fillCategoryTable() {
        Category category1 = new Category("Programming");
        addCategory(category1);
        Category category2 = new Category("Mathematics");
        addCategory(category2);
        Category category3 = new Category("GeoGraphy");
        addCategory(category3);
    }

    public void addCategory(Category category){
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizContract.Category.NAME_COLUMN,category.getName());
        db.insert(QuizContract.Category.TABLE_NAME,null,contentValues);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists " + QuizContract.QuestionTable.TABLE_NAME);
        db.execSQL("Drop table if exists " + QuizContract.Category.TABLE_NAME);
        onCreate(db);
    }

    public void fillQuestionsTable(){
        Question question1 =  new Question("GeoGraphy , Easy : A is answer","A","B","C",1,Question.DIFFICULTY_EASY, Category.GEOGRAPHY);
        addQuestions(question1);

        Question question2 =  new Question("Math , Medium : B is answer","A","B","C",2,Question.DIFFICULTY_MEDIUM,Category.MATHEMATICS);
        addQuestions(question2);

        Question question3 =  new Question("Math , Medium : B is answer","A","B","C",2,Question.DIFFICULTY_MEDIUM,Category.MATHEMATICS);
        addQuestions(question3);

        Question question4 =  new Question("Programming , Hard : C is answer","A","B","C",3,Question.DIFFICULTY_HARD,Category.PROGRAMMING);
        addQuestions(question4);

        Question question5 =  new Question("Programming , Hard : C is answer","A","B","C",3,Question.DIFFICULTY_HARD,Category.PROGRAMMING);
        addQuestions(question5);

        Question question6 =  new Question("Programming , Hard : C is answer","A","B","C",3,Question.DIFFICULTY_HARD,Category.PROGRAMMING);
        addQuestions(question6);
    }

    public void addQuestions(Question question){

        ContentValues contentValues = new ContentValues();

        contentValues.put(QuizContract.QuestionTable.QUESTION_COLUMN,question.getQuestion());
        contentValues.put(QuizContract.QuestionTable.OPTION1_COLUMN,question.getOption1());
        contentValues.put(QuizContract.QuestionTable.OPTION2_COLUMN,question.getOption2());
        contentValues.put(QuizContract.QuestionTable.OPTION3_COLUMN,question.getOption3());
        contentValues.put(QuizContract.QuestionTable.ANSWER_COLUMN,question.getAnswer());
        contentValues.put(QuizContract.QuestionTable.DIFFICULTY_COULUM,question.getDifficulty());
        contentValues.put(QuizContract.QuestionTable.CATEGORY_COLUMN,question.getCategory());

        db.insert(QuizContract.QuestionTable.TABLE_NAME,null,contentValues);
    }

    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + QuizContract.Category.TABLE_NAME,null);
        if(cursor.moveToFirst()){
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(QuizContract.Category._ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(QuizContract.Category.NAME_COLUMN)));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categoryList;
    }

    public List<Question> getAllQuestions(){
        List<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor =  db.rawQuery("Select * from " + QuizContract.QuestionTable.TABLE_NAME , null);

        if(cursor.moveToFirst()){
            do {
                Question question = new Question();
                question.setId(cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionTable._ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.QUESTION_COLUMN)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.OPTION1_COLUMN)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.OPTION2_COLUMN)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.OPTION3_COLUMN)));
                question.setAnswer(cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionTable.ANSWER_COLUMN)));
                question.setDifficulty(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.DIFFICULTY_COULUM)));
                question.setCategory(cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionTable.CATEGORY_COLUMN)));
                questionList.add(question);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return questionList;
    }

    public List<Question> getQuestions(int categoryId, String difficulty){
        List<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        String[] level = new String[]{difficulty, String.valueOf(categoryId)};

        Cursor cursor =  db.rawQuery("Select * from " + QuizContract.QuestionTable.TABLE_NAME + " where " + QuizContract.QuestionTable.DIFFICULTY_COULUM + " = ? AND " + QuizContract.QuestionTable.CATEGORY_COLUMN + " = ? "  , level);

        if(cursor.moveToFirst()){
            do {
                Question question = new Question();

                question.setId(cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionTable._ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.QUESTION_COLUMN)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.OPTION1_COLUMN)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.OPTION2_COLUMN)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.OPTION3_COLUMN)));
                question.setAnswer(cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionTable.ANSWER_COLUMN)));
                question.setDifficulty(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionTable.DIFFICULTY_COULUM)));
                question.setCategory(cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionTable.CATEGORY_COLUMN)));
                questionList.add(question);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return questionList;
    }

}
