package com.hamlet.quizapp;

import android.provider.BaseColumns;

public final class QuizContract {

    public QuizContract(){}


    public final class Category implements BaseColumns {
        public static final String TABLE_NAME = "category_table";
        public static final String NAME_COLUMN = "name_column";

    }

    public final class QuestionTable implements BaseColumns {

        public static final String TABLE_NAME = "questions_table";
        public static final String QUESTION_COLUMN = "question_column";
        public static final String  OPTION1_COLUMN = "option1_column";
        public static final String  OPTION2_COLUMN = "option2_column";
        public static final String  OPTION3_COLUMN = "option3_column";
        public static final String  ANSWER_COLUMN = "answer_column";
        public static final String DIFFICULTY_COULUM = "difficulty_column";
        public static final String CATEGORY_COLUMN = "category_id_column";

    }

}
