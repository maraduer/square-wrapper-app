package gmu.rqr.square_wrapper_app;

//Defines Product objects. This could be parent class of CheckoutProduct
public class Product {

    //ID is created by database.
    private int productID;
    private String productName;
    private double productPrice;

    public Product(String productName, double productPrice) {
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public Product() {
        //This was used in IT315 book to mark whether object should be created or updated. We may not need it
        productID = -1;
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

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }
}
