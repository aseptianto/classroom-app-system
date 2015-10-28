package hk.org.hongchi.orienteering.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hk.org.hongchi.orienteering.DDPService;

/**
 * Created by user on 10/28/2015.
 */
public class Place implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
    private Map<String, Object> fields;
    private String id;

    public Place(String id, Map<String, Object> fields) {
        this.fields = fields;
        this.id = id;

        updateFields();
    }

    protected Place(Parcel in) {
        id = in.readString();

        fields = new Gson().fromJson(in.readString(), Map.class);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return (String) fields.get("name");
    }

    public String getDirections() {
        return (String) fields.get("direction");
    }

    public List<Question> getQuestions() {
        List<Question> questions = new ArrayList<Question>();

        for (Map<String, Object> oid : (ArrayList<Map<String, Object>>) fields.get("questions")) {
            questions.add(DDPService.getInstance().getQuestion((String) oid.get("$value")));
        }

        return questions;
    }

    public double getLatitude() {
        return (double) ((Map<String, Object>) fields.get("location")).get("latitude");
    }

    public double getLongitude() {
        return (double) ((Map<String, Object>) fields.get("location")).get("longitude");
    }

    public LatLng toLatLng() {
        return new LatLng(getLatitude(), getLongitude());
    }

    public void updateFields() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        return id.equals(place.id);

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