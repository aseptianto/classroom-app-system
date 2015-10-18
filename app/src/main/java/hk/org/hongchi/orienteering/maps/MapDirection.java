package hk.org.hongchi.orienteering.maps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by user on 10/16/2015.
 */
public class MapDirection {
    private String name, description;
    private double latitude, longitude;

    public MapDirection(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public MapDirection(String name, String description, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng toLatLng() {
        return new LatLng(latitude, longitude);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
