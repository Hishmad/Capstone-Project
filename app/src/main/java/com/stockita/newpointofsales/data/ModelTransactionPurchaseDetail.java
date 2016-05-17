package com.stockita.newpointofsales.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stockita.newpointofsales.utilities.Constant;

import java.util.HashMap;

/**
 * Created by hishmadabubakaralamudi on 1/5/16.
 */
public class ModelTransactionPurchaseDetail implements Parcelable {

    // state
    private int _id;
    private String date;
    private String itemNumber;
    private String itemDescription;
    private String unitOfMeasure;
    private String currency;
    private String category;
    private String price;
    private String discount;
    private String discountValue;
    private String quantity;
    private String subTotalPerItem;
    private String invoiceNumber;
    private String headerID;
    private String timeStamps;
    private String typeOfPayment;
    private String itemMasterPushId;
    private HashMap<String, Object> timestampCreated;

    /**
     * Constructor
     */
    public ModelTransactionPurchaseDetail() {}

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSubTotalPerItem() {
        return subTotalPerItem;
    }

    public void setSubTotalPerItem(String subTotalPerItem) {
        this.subTotalPerItem = subTotalPerItem;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getHeaderID() {
        return headerID;
    }

    public void setHeaderID(String headerID) {
        this.headerID = headerID;
    }

    public String getTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(String timeStamps) {
        this.timeStamps = timeStamps;
    }

    public String getTypeOfPayment() {
        return typeOfPayment;
    }

    public void setTypeOfPayment(String typeOfPayment) {
        this.typeOfPayment = typeOfPayment;
    }

    public String getItemMasterPushId() {
        return itemMasterPushId;
    }

    public void setItemMasterPushId(String itemMasterPushId) {
        this.itemMasterPushId = itemMasterPushId;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    @JsonIgnore
    public long getTimestampCreatedLong() {
        return (long) timestampCreated.get(Constant.FIREBASE_PROPERTY_TIMESTAMP);
    }

    protected ModelTransactionPurchaseDetail(Parcel in) {
        _id = in.readInt();
        date = in.readString();
        itemNumber = in.readString();
        itemDescription = in.readString();
        unitOfMeasure = in.readString();
        currency = in.readString();
        category = in.readString();
        price = in.readString();
        discount = in.readString();
        discountValue = in.readString();
        quantity = in.readString();
        subTotalPerItem = in.readString();
        invoiceNumber = in.readString();
        headerID = in.readString();
        timeStamps = in.readString();
        typeOfPayment = in.readString();
        timestampCreated = (HashMap) in.readValue(HashMap.class.getClassLoader());
        itemMasterPushId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(date);
        dest.writeString(itemNumber);
        dest.writeString(itemDescription);
        dest.writeString(unitOfMeasure);
        dest.writeString(currency);
        dest.writeString(category);
        dest.writeString(price);
        dest.writeString(discount);
        dest.writeString(discountValue);
        dest.writeString(quantity);
        dest.writeString(subTotalPerItem);
        dest.writeString(invoiceNumber);
        dest.writeString(headerID);
        dest.writeString(timeStamps);
        dest.writeString(typeOfPayment);
        dest.writeValue(timestampCreated);
        dest.writeString(itemMasterPushId);
    }

    @SuppressWarnings("unused")
    public static final Creator<ModelTransactionPurchaseDetail> CREATOR = new Creator<ModelTransactionPurchaseDetail>() {
        @Override
        public ModelTransactionPurchaseDetail createFromParcel(Parcel in) {
            return new ModelTransactionPurchaseDetail(in);
        }

        @Override
        public ModelTransactionPurchaseDetail[] newArray(int size) {
            return new ModelTransactionPurchaseDetail[size];
        }
    };
}
