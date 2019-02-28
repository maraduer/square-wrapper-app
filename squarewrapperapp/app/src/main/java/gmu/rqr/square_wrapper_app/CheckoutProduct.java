package gmu.rqr.square_wrapper_app;


import java.io.Serializable;

public class CheckoutProduct implements Serializable {

    private String productName;
    private double productPrice;
    private double productWeight;


    public double getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(double productWeight) {
        this.productWeight = productWeight;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

}
