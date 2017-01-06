package com.prueba.diana.pruebaapplist.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Diana on 1/5/2017.
 */

public class Applications implements Parcelable {

    public static final String TABLE_NAME = "APPLICATIONS";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_THUMBNAIL_URL_M = "THUMBNAIL_URL_M";
    public static final String COLUMN_THUMBNAIL_URL_B = "THUMBNAIL_URL_B";
    public static final String COLUMN_SUMMARY = "SUMMARY";
    public static final String COLUMN_PRICE = "PRICE";
    public static final String COLUMN_RIGHTS = "RIGHTS";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_LINK = "LINK";
    public static final String COLUMN_CATEGORY = "CATEGORY";
    public static final String COLUMN_REALESE_DATE = "REALESE_DATE";
    public static final String COLUMN_ARTIST = "ARTIST";

    public int app_id;
    public double price;
    public String pictureM;
    public String pictureB;
    public String summary;
    public String title;
    public String artist;
    public String name;
    public String rights;
    public String category;
    public String realese_date;
    public String last_date;

    public Applications() {
    }

    public Applications (Parcel in) {
        readFromParcel(in);
    }

    public int getApp_id() {
        return app_id;
    }

    public void setApp_id(int app_id) {
        this.app_id = app_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPictureM() {
        return pictureM;
    }

    public void setPictureM(String pictureM) {
        this.pictureM = pictureM;
    }

    public String getPictureB() {
        return pictureB;
    }

    public void setPictureB(String pictureB) {
        this.pictureB = pictureB;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRealese_date() {
        return realese_date;
    }

    public void setRealese_date(String realese_date) {
        this.realese_date = realese_date;
    }

    public String getLast_date() {
        return last_date;
    }

    public void setLast_date(String last_date) {
        this.last_date = last_date;
    }


    private void readFromParcel(Parcel in) {
        // We just need to read back each
        // field in the order that it was
        // written to the parcel
        app_id = in.readInt();
        artist = in.readString();
        price = in.readDouble();
        realese_date = in.readString();
        summary = in.readString();
        category = in.readString();
        name = in.readString();
        title = in.readString();
        rights = in.readString();
        pictureM = in.readString();
        pictureB = in.readString();

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Applications createFromParcel(Parcel in) {
            return new Applications(in);
        }
        public Applications[] newArray(int size) {
            return new Applications[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // We just need to write each field into the
        // parcel. When we read from parcel, they
        // will come back in the same order
        dest.writeInt(app_id);
        dest.writeString(artist);
        dest.writeDouble(price);
        dest.writeString(realese_date);
        dest.writeString(summary);
        dest.writeString(category);
        dest.writeString(name);
        dest.writeString(rights);
        dest.writeString(title);
        dest.writeString(pictureM);
        dest.writeString(pictureB);

    }
}
