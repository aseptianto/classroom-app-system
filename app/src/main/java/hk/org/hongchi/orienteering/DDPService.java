package hk.org.hongchi.orienteering;

import android.content.Context;

import com.keysolutions.ddpclient.DDPClient.DdpMessageType;
import com.keysolutions.ddpclient.android.DDPStateSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hk.org.hongchi.orienteering.models.Place;
import hk.org.hongchi.orienteering.models.Question;

/**
 * Created by user on 10/27/2015.
 */
public class DDPService extends DDPStateSingleton {
    private List<Place> places = new ArrayList<Place>();
    private List<Question> questions = new ArrayList<Question>();

    private DDPService(Context context) {
        super(context, "iniasep.meteor.com", DDPStateSingleton.sMeteorPort);
    }

    public static void initInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DDPService(context);
        }
    }

    public static DDPService getInstance() {
        return (DDPService) mInstance;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public Place getPlace(String placeId) {
        for (Place p : places) {
            if (p.getId().equals(placeId))
                return p;
        }
        return null;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public Question getQuestion(String questionId) {
        for (Question q : questions) {
            if (q.getId().equals(questionId)) {
                return q;
            }
        }
        return null;
    }

    @Override
    public void broadcastSubscriptionChanged(String collectionName,
                                             String changeType, String docId) {
        if (collectionName.equals("place")) {
            if (changeType.equals(DdpMessageType.ADDED)) {
                Map<String, Object> properties = getCollection(collectionName).get(docId);
                places.add(new Place(docId, properties));
            } else if (changeType.equals(DdpMessageType.REMOVED)) {
                places.remove(getPlace(docId));
            } else if (changeType.equals(DdpMessageType.UPDATED)) {
                getPlace(docId).updateFields();
            }
        }

        if (collectionName.equals("question")) {
            if (changeType.equals(DdpMessageType.ADDED)) {
                Map<String, Object> properties = getCollection(collectionName).get(docId);
                questions.add(new Question(docId, properties));
            } else if (changeType.equals(DdpMessageType.REMOVED)) {
                questions.remove(getQuestion(docId));
            } else if (changeType.equals(DdpMessageType.UPDATED)) {
                getQuestion(docId).updateFields();
            }
        }

        super.broadcastSubscriptionChanged(collectionName, changeType, docId);
    }
}