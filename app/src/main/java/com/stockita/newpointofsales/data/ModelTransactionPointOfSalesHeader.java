package com.stockita.newpointofsales.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stockita.newpointofsales.utilities.Constant;

import java.util.HashMap;

/**
 * Class Model for sales header
 */
public class ModelTransactionPointOfSalesHeader implements Parcelable {


    // State
    private String invoiceDiscount; // mInvoiceDiscount
    private String invoiceTax; // mInvoiceTax
    private String taxValue; // mTaxValue
    private String subTotalBeforeTax;
    private String invoiceTotalAfterTax; // mInvoiceTotalAfterTax
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String typeOfPayment;
    private String invoiceNumber;
    private String numberOfItems;
    private String discountValue; //mDiscountValue
    private String timeStamps;
    private HashMap<String, Object> timestampCreated;


    // Constructor
    public ModelTransactionPointOfSalesHeader() {}


    public String getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(String taxValue) {
        this.taxValue = taxValue;
    }

    public String getTypeOfPayment() {
        return typeOfPayment;
    }

    public void setTypeOfPayment(String typeOfPayment) {
        this.typeOfPayment = typeOfPayment;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }


    public String getInvoiceDiscount() {
        return invoiceDiscount;
    }

    public void setInvoiceDiscount(String invoiceDiscount) {
        this.invoiceDiscount = invoiceDiscount;
    }

    public String getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(String numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public String getInvoiceTax() {
        return invoiceTax;
    }

    public void setInvoiceTax(String invoiceTax) {
        this.invoiceTax = invoiceTax;
    }

    public String getSubTotalBeforeTax() {
        return subTotalBeforeTax;
    }

    public void setSubTotalBeforeTax(String subTotalBeforeTax) {
        this.subTotalBeforeTax = subTotalBeforeTax;
    }

    public String getInvoiceTotalAfterTax() {
        return invoiceTotalAfterTax;
    }

    public void setInvoiceTotalAfterTax(String invoiceTotalAfterTax) {
        this.invoiceTotalAfterTax = invoiceTotalAfterTax;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }


    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public String getTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(String timeStamps) {
        this.timeStamps = timeStamps;
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

    protected ModelTransactionPointOfSalesHeader(Parcel in) {

        invoiceDiscount = in.readString();
        invoiceTax = in.readString();
        taxValue = in.readString();
        subTotalBeforeTax = in.readString();
        invoiceTotalAfterTax = in.readString();
        customerName = in.readString();
        customerEmail = in.readString();
        customerPhone = in.readString();
        typeOfPayment = in.readString();
        invoiceNumber = in.readString();
        numberOfItems = in.readString();
        discountValue = in.readString();
        timeStamps = in.readString();
        timestampCreated = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(invoiceDiscount);
        dest.writeString(invoiceTax);
        dest.writeString(taxValue);
        dest.writeString(subTotalBeforeTax);
        dest.writeString(invoiceTotalAfterTax);
        dest.writeString(customerName);
        dest.writeString(customerEmail);
        dest.writeString(customerPhone);
        dest.writeString(typeOfPayment);
        dest.writeString(invoiceNumber);
        dest.writeString(numberOfItems);
        dest.writeString(discountValue);
        dest.writeString(timeStamps);
        dest.writeValue(timestampCreated);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ModelTransactionPointOfSalesHeader> CREATOR = new Parcelable.Creator<ModelTransactionPointOfSalesHeader>() {
        @Override
        public ModelTransactionPointOfSalesHeader createFromParcel(Parcel in) {
            return new ModelTransactionPointOfSalesHeader(in);
        }

        @Override
        public ModelTransactionPointOfSalesHeader[] newArray(int size) {
            return new ModelTransactionPointOfSalesHeader[size];
        }
    };
}
