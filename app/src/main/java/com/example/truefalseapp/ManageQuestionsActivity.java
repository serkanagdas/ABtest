package com.example.truefalseapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageQuestionsActivity extends AppCompatActivity {
    private QuestionDatabase db;
    private QuestionDao questionDao;
    private EditText editTextQuestion, editTextAnswer, editTextCountdown;
    private Button buttonAdd, buttonRemove, buttonReturnToMain, buttonSetCountdown, buttonResetQuestions;
    private RecyclerView recyclerViewQuestions;
    private QuestionAdapter questionAdapter;
    private int countdownTime = 30; // Varsayılan geri sayım süresi
    private ExecutorService executorService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_questions);

        db = QuestionDatabase.getDatabase(this);
        questionDao = db.questionDao();
        executorService = Executors.newSingleThreadExecutor();

        editTextQuestion = findViewById(R.id.editTextQuestion);
        editTextAnswer = findViewById(R.id.editTextAnswer);
        editTextCountdown = findViewById(R.id.editTextCountdown);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonRemove = findViewById(R.id.buttonRemove);
        buttonReturnToMain = findViewById(R.id.buttonReturnToMain);
        buttonSetCountdown = findViewById(R.id.buttonSetCountdown);
        buttonResetQuestions = findViewById(R.id.buttonResetQuestions);
        recyclerViewQuestions = findViewById(R.id.recyclerViewQuestions);

        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new QuestionAdapter();
        recyclerViewQuestions.setAdapter(questionAdapter);

        loadQuestions();

        buttonSetCountdown.setOnClickListener(v -> {
            String countdownText = editTextCountdown.getText().toString();
            if (!countdownText.isEmpty()) {
                try {
                    countdownTime = Integer.parseInt(countdownText);
                    Toast.makeText(this, "Countdown set to: " + countdownTime + " seconds", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid number for countdown", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a countdown time", Toast.LENGTH_SHORT).show();
            }
        });

        buttonAdd.setOnClickListener(v -> {
            String questionText = editTextQuestion.getText().toString();
            String answerText = editTextAnswer.getText().toString();
            if (!questionText.isEmpty() && !answerText.isEmpty()) {
                executorService.execute(() -> {
                    Question question = new Question();
                    question.setQuestion(questionText);
                    question.setAnswer(answerText);
                    question.setCountdownTime(countdownTime); // Geri sayım süresini ayarla
                    questionDao.insert(question);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Question added with countdown: " + countdownTime + " seconds", Toast.LENGTH_SHORT).show();
                        loadQuestions();
                    });
                });
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        buttonRemove.setOnClickListener(v -> {
            String questionText = editTextQuestion.getText().toString();
            executorService.execute(() -> {
                List<Question> questions = questionDao.getAllQuestions();
                for (Question q : questions) {
                    if (q.getQuestion().equals(questionText)) {
                        questionDao.delete(q);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Question removed", Toast.LENGTH_SHORT).show();
                            loadQuestions();
                        });
                        break;
                    }
                }
            });
        });

        buttonReturnToMain.setOnClickListener(v -> {
            finish();
        });

        buttonResetQuestions.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Tüm soruları sıfırlamak istiyor musunuz?")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        executorService.execute(() -> {
                            questionDao.deleteAll();
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Tüm sorular silindi", Toast.LENGTH_SHORT).show();
                                loadQuestions();
                            });
                        });
                    })
                    .setNegativeButton("Hayır", null)
                    .show();
        });
    }

    private void loadQuestions() {
        executorService.execute(() -> {
            List<Question> questions = questionDao.getAllQuestions();
            runOnUiThread(() -> questionAdapter.setQuestions(questions));
        });
    }
}