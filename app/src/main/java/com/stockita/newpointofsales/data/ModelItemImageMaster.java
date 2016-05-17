package com.stockita.newpointofsales.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model for Item Image Master
 */
public class ModelItemImageMaster implements Parcelable {

    private String itemNumber;
    private String imageUrl;
    private String imageName;

    public ModelItemImageMaster() {}

    public ModelItemImageMaster(String itemNumber, String imageUrl, String imageName) {
        this.itemNumber = itemNumber;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
    }


    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    protected ModelItemImageMaster(Parcel in) {
        itemNumber = in.readString();
        imageUrl = in.readString();
        imageName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemNumber);
        dest.writeString(imageUrl);
        dest.writeString(imageName);
    }

    @SuppressWarnings("unused")
    public static final Creator<ModelItemImageMaster> CREATOR = new Creator<ModelItemImageMaster>() {
        @Override
        public ModelItemImageMaster createFromParcel(Parcel in) {
            return new ModelItemImageMaster(in);
        }

        @Override
        public ModelItemImageMaster[] newArray(int size) {
            return new ModelItemImageMaster[size];
        }
    };
}
