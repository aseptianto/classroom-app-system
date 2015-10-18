package hk.org.hongchi.orienteering;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class QuizActivity extends AppCompatActivity {
    public static final int QUIZ_MULTIPLE_CHOICE = 0;
    public static final int QUIZ_FREE_RESPONSE = 3;
    public static final int QUIZ_PHOTO = 1;
    public static final int QUIZ_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (getIntent().getIntExtra("quizType", QUIZ_MULTIPLE_CHOICE)) {
            case QUIZ_MULTIPLE_CHOICE:
                setContentView(R.layout.activity_quiz);
                break;
            default:
                System.out.println("An unsupported quiz type was requested.");
                break;
        }
    }
}
