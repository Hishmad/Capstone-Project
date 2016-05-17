package com.stockita.newpointofsales.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model class for the Co-workers list
 */
public class ModelCoworkers implements Parcelable {

    private String emailAddress;
    private String name;
    private boolean booleanStatus;
    private String jobStatus;

    /**
     * Empty constructor
     */
    public ModelCoworkers() {}

    /**
     * Constructor
     * @param emailAddress      The co-worker email address
     * @param name              The co-worker name
     * @param booleanStatus     true if accepted else false
     */
    public ModelCoworkers(String emailAddress, String name, String jobStatus, boolean booleanStatus){
        this.emailAddress = emailAddress;
        this.name = name;
        this.jobStatus = jobStatus;
        this.booleanStatus = booleanStatus;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getBooleanStatus() {
        return booleanStatus;
    }

    public void setBooleanStatus(boolean booleanStatus) {
        this.booleanStatus = booleanStatus;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    protected ModelCoworkers(Parcel in) {
        emailAddress = in.readString();
        name = in.readString();
        jobStatus = in.readString();
        booleanStatus = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(emailAddress);
        dest.writeString(name);
        dest.writeString(jobStatus);
        dest.writeByte((byte) (booleanStatus ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ModelCoworkers> CREATOR = new Parcelable.Creator<ModelCoworkers>() {
        @Override
        public ModelCoworkers createFromParcel(Parcel in) {
            return new ModelCoworkers(in);
        }

        @Override
        public ModelCoworkers[] newArray(int size) {
            return new ModelCoworkers[size];
        }
    };
}
