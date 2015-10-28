package hk.org.hongchi.orienteering.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hk.org.hongchi.orienteering.DDPService;

/**
 * Created by user on 10/28/2015.
 */
public class Question implements Parcelable {
    public static final int Q_MULTIPLE_CHOICE = 0;
    public static final int Q_FREE_RESPONSE = 1;
    public static final int Q_PHOTO = 2;
    public static final int Q_VIDEO = 3;
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
    private Map<String, Object> fields;
    private String id;

    public Question(String id, Map<String, Object> fields) {
        this.fields = fields;
        this.id = id;

        updateFields();
    }

    protected Question(Parcel in) {
        id = in.readString();

        fields = new Gson().fromJson(in.readString(), Map.class);
    }

    public String getId() {
        return id;
    }

    public Place getPlace() {
        return DDPService.getInstance().getPlace((String) fields.get("place"));
    }

    public String getPrompt() {
        return (String) fields.get("prompt");
    }

    public int getQuestionType() {
        return Integer.parseInt((String) ((Map<String, Object>) fields.get("choices")).get("type"));
    }

    public List<String> getChoices() {
        return (ArrayList<String>) ((Map<String, Object>) fields.get("choices")).get("data");
    }

    public void updateFields() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        return id.equals(question.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(new Gson().toJson(fields));
    }
}