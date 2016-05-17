package com.stockita.newpointofsales.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;
import com.stockita.newpointofsales.utilities.Constant;

import java.util.HashMap;

/**
 * Created by hishmadabubakaralamudi on 11/18/15.
 */
public class ModelCategoryMaster implements Parcelable {

    // State
    private int _id;
    private String category;
    private HashMap<String, Object> timestampLastChanged;
    private HashMap<String, Object> timestampCreated;

    /**
     * Empty constructor
     */
    public ModelCategoryMaster() {}

    /**
     * Use this constructor to create new Category.
     *
     * @param id                    The ID of SQLite local database
     * @param category              Category
     * @param timestampCreated      Firebase ServerValue.TIMESTAMP
     */
    public ModelCategoryMaster(int id, String category, HashMap<String, Object> timestampCreated) {
        this._id = id;
        this.category = category;
        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constant.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;

    }

    // Methods
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    @JsonIgnore
    public long getTimestampLastChangedLong() {

        return (long) timestampLastChanged.get(Constant.FIREBASE_PROPERTY_TIMESTAMP);
    }

    @JsonIgnore
    public long getTimestampCreatedLong() {
        return (long) timestampCreated.get(Constant.FIREBASE_PROPERTY_TIMESTAMP);
    }

    protected ModelCategoryMaster(Parcel in) {
        _id = in.readInt();
        category = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(category);
    }

    @SuppressWarnings("unused")
    public static final Creator<ModelCategoryMaster> CREATOR = new Creator<ModelCategoryMaster>() {
        @Override
        public ModelCategoryMaster createFromParcel(Parcel in) {
            return new ModelCategoryMaster(in);
        }

        @Override
        public ModelCategoryMaster[] newArray(int size) {
            return new ModelCategoryMaster[size];
        }
    };
}
