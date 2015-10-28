package hk.org.hongchi.orienteering;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import hk.org.hongchi.orienteering.models.Place;

public class QuizActivity extends AppCompatActivity {
    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        place = getIntent().getParcelableExtra("place");

        setContentView(R.layout.activity_quiz);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new QuestionPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
    }

    private class QuestionPagerAdapter extends FragmentStatePagerAdapter {
        public QuestionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            QuestionFragment questionFragment = new QuestionFragment();
            Bundle arguments = new Bundle();
            arguments.putParcelable("question", place.getQuestions().get(position));
            questionFragment.setArguments(arguments);

            return questionFragment;
        }

        @Override
        public int getCount() {
            return place.getQuestions().size();
        }
    }
}
