package hk.org.hongchi.orienteering;

/**
 * Created by user on 10/16/2015.
 */
public class MapDirection {
    private String name, description;
    private long latitude, longitude;

    public MapDirection(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public MapDirection(String name, String description, long latitude, long longitude) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }
}
