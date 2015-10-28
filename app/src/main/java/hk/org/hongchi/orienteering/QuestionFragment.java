package hk.org.hongchi.orienteering;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import hk.org.hongchi.orienteering.models.Question;

/**
 * Created by user on 10/28/2015.
 */
public class QuestionFragment extends Fragment {
    private Question question;

    private Button btnSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        question = getArguments().getParcelable("question");

        ViewGroup rootView;
        switch (question.getQuestionType()) {
            case Question.Q_FREE_RESPONSE:
                rootView = (ViewGroup) inflater.inflate(R.layout.fragment_question_fr, container, false);
                break;
            default:
                rootView = (ViewGroup) inflater.inflate(R.layout.fragment_question_mc, container, false);

                ListView choicesList = (ListView) rootView.findViewById(R.id.quiz_choices);
                choicesList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, question.getChoices()));
                choicesList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                choicesList.setSelector(android.R.color.holo_blue_light);
                choicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        view.setSelected(true);
                    }
                });
                break;
        }

        TextView txtPrompt = (TextView) rootView.findViewById(R.id.quiz_prompt);
        txtPrompt.setText(question.getPrompt());

        btnSubmit = (Button) rootView.findViewById(R.id.submit_button);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return rootView;
    }
}