package com.example.truefalseapp;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Question.class}, version = 2)
public abstract class QuestionDatabase extends RoomDatabase {
    public abstract QuestionDao questionDao();

    private static volatile QuestionDatabase INSTANCE;

    static QuestionDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (QuestionDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    QuestionDatabase.class, "question_database")
                            .fallbackToDestructiveMigration() // Geriye dönük uyumsuzluk durumunda veritabanını sıfırla
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}