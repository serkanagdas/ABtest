package com.example.truefalseapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private TextView textViewTimer, TextViewQuestion, TextViewScore, TextViewAttempts, txtcounter, txtrueorfalse;
    private Button buttonOption1, buttonOption2, buttonManageQuestions;

    private int score = 0;
    private DatabaseHelper dbHelper;
    private int attempts = 3;
    private CountDownTimer countDownTimer;
    private int counter = 1;
    private int questionCount = 0;
    private HashSet<Integer> askedQuestions = new HashSet<>();
    private QuestionDatabase db;
    private QuestionDao questionDao;
    private List<Question> questionList;
    private CountDownTimer timer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Görünümleri başlat
        textViewTimer = findViewById(R.id.textGeriSayım1);
        TextViewQuestion = findViewById(R.id.txtcount1);
        TextViewScore = findViewById(R.id.txtpuan1);
        TextViewAttempts = findViewById(R.id.texthak1);
        txtcounter = findViewById(R.id.txtcounter);
        txtrueorfalse = findViewById(R.id.txtrueorfalse);
        buttonOption1 = findViewById(R.id.cevap1);
        buttonOption2 = findViewById(R.id.cevap2);
        buttonManageQuestions = findViewById(R.id.buttonManageQuestions);

        // Veritabanını başlat
        db = QuestionDatabase.getDatabase(getApplicationContext());
        questionDao = db.questionDao();

        // Veritabanı oluşturma
        dbHelper = new DatabaseHelper(this);
        dbHelper.getWritableDatabase();

        // Soruları yeni bir thread ile yükle
        loadQuestions();

        // Manage Questions butonu için OnClickListener ayarla
        buttonManageQuestions.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManageQuestionsActivity.class);
            startActivity(intent);
        });
    }

    private void loadQuestions() {
        new Thread(() -> {
            try {
                questionList = questionDao.getAllQuestions();
                runOnUiThread(() -> {
                    if (questionList != null && !questionList.isEmpty()) {
                        startGame();
                    } else {
                        Toast.makeText(this, "No questions available", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                Log.e("MainActivity", "Error loading questions: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading questions", Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void CounterInt() {
        if (counter <= questionList.size()) {
            counter++;
            txtcounter.setText("" + counter + "/" + 20);
        }
    }

    private void startGame() {
        questionCount = 0;
        score = 0;
        askedQuestions = new HashSet<>();

        // Timer'ı başlat
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (textViewTimer != null) {
                    textViewTimer.setText("Geri Sayım: " + millisUntilFinished / 1000);
                }
            }

            @Override
            public void onFinish() {
                if (textViewTimer != null) {
                    textViewTimer.setText("Süre Bitti");
                    endGame();
                }
            }
        }.start();

        updateQuestion();
    }

    int correctAnswerIndex;

    private void updateQuestion() {
        Log.d("MainActivity", "updateQuestion called");

        if (questionCount >= 20) {
            endGame();
            return;
        }

        if (questionList == null || questionList.isEmpty()) {
            Toast.makeText(this, "No questions available", Toast.LENGTH_LONG).show();
            return;
        }

        Random random = new Random();
        int randomIndex;
        do {
            randomIndex = random.nextInt(questionList.size());
        } while (askedQuestions.contains(randomIndex));

        askedQuestions.add(randomIndex);
        questionCount++;
        txtcounter.setText(questionCount + "/20");

        Question currentQuestion = questionList.get(randomIndex);
        TextViewQuestion.setText(currentQuestion.getQuestion());
        correctAnswerIndex = randomIndex;
        int wrongAnswerIndex = (randomIndex + 1) % questionList.size();

        if (random.nextBoolean()) {
            buttonOption1.setText(questionList.get(correctAnswerIndex).getAnswer());
            buttonOption2.setText(questionList.get(wrongAnswerIndex).getAnswer());
        } else {
            buttonOption1.setText(questionList.get(wrongAnswerIndex).getAnswer());
            buttonOption2.setText(questionList.get(correctAnswerIndex).getAnswer());
        }

        buttonOption1.setOnClickListener(v -> {
            Log.d("MainActivity", "Button 1 clicked");
            checkAnswer(buttonOption1.getText().toString().equals(questionList.get(correctAnswerIndex).getAnswer()));
        });
        buttonOption2.setOnClickListener(v -> {
            Log.d("MainActivity", "Button 2 clicked");
            checkAnswer(buttonOption2.getText().toString().equals(questionList.get(correctAnswerIndex).getAnswer()));
        });

        Log.d("MainActivity", "Question updated: " + currentQuestion.getQuestion());
    }

    private int answerTrue;
    private int answerFalse;

    private void checkAnswer(boolean selectedOption1IsCorrect) {
        Log.d("MainActivity", "checkAnswer called");

        if (selectedOption1IsCorrect) {
            score += 10;
            answerTrue++;
            TextViewScore.setText("" + score);
            Toast.makeText(this, "Tebrikler Doğru Cevap", Toast.LENGTH_LONG).show();
            txtrueorfalse.setBackgroundColor(Color.GREEN);
            txtrueorfalse.setTextColor(Color.BLACK);
            txtrueorfalse.setText("Cevap Doğru : " + questionList.get(correctAnswerIndex).getAnswer());
        } else {
            attempts--;
            answerFalse++;
            TextViewAttempts.setText("" + attempts);
            if (attempts == 0) {
                endGame();
                return;
            }
            Toast.makeText(this, "Maalesef yanlış cevap verdiniz!" + attempts, Toast.LENGTH_LONG).show();
            txtrueorfalse.setBackgroundColor(Color.RED);
            txtrueorfalse.setTextColor(Color.WHITE);
            txtrueorfalse.setText("Yanlış Cevap  : " + questionList.get(correctAnswerIndex).getAnswer());
        }

        updateQuestion();
        Log.d("MainActivity", "checkAnswer completed");
    }

    private void endGame() {
        if (timer != null) {
            timer.cancel();
        }
        Intent i = new Intent(MainActivity.this, EndGamePage.class);
        i.putExtra("score", "" + score);
        i.putExtra("answerTrue", "" + answerTrue);
        i.putExtra("answerFalse", "" + answerFalse);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}