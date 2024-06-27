package model;

import android.os.Parcel;
import android.os.Parcelable;

public class Talent implements Parcelable {
    private String id;
    private String name;
    private String branch;
    private String debut;
    private String image;
    private String bioData;

    public Talent() {
        // Default constructor required for Firebase
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDebut() {
        return debut;
    }

    public void setDebut(String debut) {
        this.debut = debut;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBioData() {
        return bioData;
    }

    public void setBioData(String bioData) {
        this.bioData = bioData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.branch);
        dest.writeString(this.debut);
        dest.writeString(this.image);
        dest.writeString(this.bioData);
    }

    protected Talent(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.branch = in.readString();
        this.debut = in.readString();
        this.image = in.readString();
        this.bioData = in.readString();
    }

    public static final Parcelable.Creator<Talent> CREATOR = new Parcelable.Creator<Talent>() {
        @Override
        public Talent createFromParcel(Parcel source) {
            return new Talent(source);
        }

        @Override
        public Talent[] newArray(int size) {
            return new Talent[size];
        }
    };
}
