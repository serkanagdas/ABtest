package com.example.truefalseapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuestionDao {
    @Insert
    void insert(Question question);

    @Delete
    void delete(Question question);

    @Query("DELETE FROM question_table")
    void deleteAll();

    @Query("SELECT * FROM question_table")
    List<Question> getAllQuestions();
}