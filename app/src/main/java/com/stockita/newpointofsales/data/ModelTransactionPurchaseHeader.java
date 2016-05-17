package com.stockita.newpointofsales.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hishmadabubakaralamudi on 1/5/16.
 */
public class ModelTransactionPurchaseHeader implements Parcelable {

    // State
    private int _id;
    private String date;
    private String invoiceDiscount;
    private String invoiceTax;
    private String taxValue;
    private String subTotalBeforeTax;
    private String invoiceTotalAfterTax;
    private String vendorName;
    private String vendorEmail;
    private String vendorAddress;
    private String vendorPhone;
    private String typeOfPayment;
    private String invoiceNumber;
    private String numberOfItems;
    private String discountValue;
    private String timeStamps;

    /**
     * Constructor
     */
    public ModelTransactionPurchaseHeader() {
    }

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

    public String getInvoiceDiscount() {
        return invoiceDiscount;
    }

    public void setInvoiceDiscount(String invoiceDiscount) {
        this.invoiceDiscount = invoiceDiscount;
    }

    public String getInvoiceTax() {
        return invoiceTax;
    }

    public void setInvoiceTax(String invoiceTax) {
        this.invoiceTax = invoiceTax;
    }

    public String getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(String taxValue) {
        this.taxValue = taxValue;
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

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    public String getVendorPhone() {
        return vendorPhone;
    }

    public void setVendorPhone(String vendorPhone) {
        this.vendorPhone = vendorPhone;
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

    public String getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(String numberOfItems) {
        this.numberOfItems = numberOfItems;
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

    protected ModelTransactionPurchaseHeader(Parcel in) {
        _id = in.readInt();
        date = in.readString();
        invoiceDiscount = in.readString();
        invoiceTax = in.readString();
        taxValue = in.readString();
        subTotalBeforeTax = in.readString();
        invoiceTotalAfterTax = in.readString();
        vendorName = in.readString();
        vendorEmail = in.readString();
        vendorAddress = in.readString();
        vendorPhone = in.readString();
        typeOfPayment = in.readString();
        invoiceNumber = in.readString();
        numberOfItems = in.readString();
        discountValue = in.readString();
        timeStamps = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(date);
        dest.writeString(invoiceDiscount);
        dest.writeString(invoiceTax);
        dest.writeString(taxValue);
        dest.writeString(subTotalBeforeTax);
        dest.writeString(invoiceTotalAfterTax);
        dest.writeString(vendorName);
        dest.writeString(vendorEmail);
        dest.writeString(vendorAddress);
        dest.writeString(vendorPhone);
        dest.writeString(typeOfPayment);
        dest.writeString(invoiceNumber);
        dest.writeString(numberOfItems);
        dest.writeString(discountValue);
        dest.writeString(timeStamps);
    }

    @SuppressWarnings("unused")
    public static final Creator<ModelTransactionPurchaseHeader> CREATOR = new Creator<ModelTransactionPurchaseHeader>() {
        @Override
        public ModelTransactionPurchaseHeader createFromParcel(Parcel in) {
            return new ModelTransactionPurchaseHeader(in);
        }

        @Override
        public ModelTransactionPurchaseHeader[] newArray(int size) {
            return new ModelTransactionPurchaseHeader[size];
        }
    };
}
