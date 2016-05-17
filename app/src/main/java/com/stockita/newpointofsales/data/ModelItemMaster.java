package com.stockita.newpointofsales.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hishmadabubakaralamudi on 11/18/15.
 */
public class ModelItemMaster implements Parcelable {

    // State
    private int _id;
    private String itemNumber;
    private String itemDescription;
    private String unitOfMeasure;
    private String category;
    private String currency;
    private float price;
    private double availableStock;
    private double stockIn;
    private double stockOut;
    private String trigger;

    // Empty Constructor
    public ModelItemMaster() {}

    // Methods
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(double availableStock) {
        this.availableStock = availableStock;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public double getStockIn() {
        return stockIn;
    }

    public void setStockIn(double stockIn) {
        this.stockIn = stockIn;
    }

    public double getStockOut() {
        return stockOut;
    }

    public void setStockOut(double stockOut) {
        this.stockOut = stockOut;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    protected ModelItemMaster(Parcel in) {
        _id = in.readInt();
        itemNumber = in.readString();
        itemDescription = in.readString();
        unitOfMeasure = in.readString();
        category = in.readString();
        currency = in.readString();
        price = in.readFloat();
        availableStock = in.readDouble();
        stockIn = in.readDouble();
        stockOut = in.readDouble();
        trigger = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(itemNumber);
        dest.writeString(itemDescription);
        dest.writeString(unitOfMeasure);
        dest.writeString(category);
        dest.writeString(currency);
        dest.writeFloat(price);
        dest.writeDouble(availableStock);
        dest.writeDouble(stockIn);
        dest.writeDouble(stockOut);
        dest.writeString(trigger);
    }

    @SuppressWarnings("unused")
    public static final Creator<ModelItemMaster> CREATOR = new Creator<ModelItemMaster>() {
        @Override
        public ModelItemMaster createFromParcel(Parcel in) {
            return new ModelItemMaster(in);
        }

        @Override
        public ModelItemMaster[] newArray(int size) {
            return new ModelItemMaster[size];
        }
    };
}
