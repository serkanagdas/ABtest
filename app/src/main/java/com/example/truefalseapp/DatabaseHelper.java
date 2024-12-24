package com.example.truefalseapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "questions.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Veritabanı oluşturma ve tablo oluşturma işlemleri
        String createTable = "CREATE TABLE questions (id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT, answer TEXT)";
        db.execSQL(createTable);

        // Varsayılan verileri ekleme
        addDefaultQuestions(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS questions");
        onCreate(db);
    }

    private void addDefaultQuestions(SQLiteDatabase db) {
        // Varsayılan 30 soruyu ekleme
        String[] questions = {
                "car", "cat", "dog", "paper", "book", "pen", "pencil", "table", "chair", "window",
                "door", "house", "tree", "flower", "grass", "sky", "star", "moon", "sun", "cloud",
                "rain", "snow", "wind", "fire", "water", "earth", "mountain", "river", "lake", "sea"
        };
        String[] answers = {
                "araba", "kedi", "köpek", "kağıt", "kitap", "kalem", "kurşun kalem", "masa", "sandalye", "pencere",
                "kapı", "ev", "ağaç", "çiçek", "çimen", "gökyüzü", "yıldız", "ay", "güneş", "bulut",
                "yağmur", "kar", "rüzgar", "ateş", "su", "toprak", "dağ", "nehir", "göl", "deniz"
        };

        for (int i = 0; i < questions.length; i++) {
            String insertQuery = "INSERT INTO questions (question, answer) VALUES ('" + questions[i] + "', '" + answers[i] + "')";
            db.execSQL(insertQuery);
        }
    }
}