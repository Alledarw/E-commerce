package product;

public class Product {
  int productId;
  String productName;
  String productDetail;
  double price;
  boolean isInStock;
  boolean isActive;

  public Product(){}

  public Product(int productId, String productName, String productDetail, double price) {
    this.productId = productId;
    this.productName = productName;
    this.productDetail = productDetail;
    this.price = price;
    this.isInStock = false;
    this.isActive = true;
  }

  public Product(int productId, String productName, String productDetail, double price, boolean isInStock, boolean isActive) {
    this.productId = productId;
    this.productName = productName;
    this.productDetail = productDetail;
    this.price = price;
    this.isInStock = isInStock;
    this.isActive = isActive;
  }

  public void updateItem(Product updateItem) {
    this.productId = updateItem.productId;
    this.productName = updateItem.productName;
    this.productDetail = updateItem.productDetail;
    this.price = updateItem.price;
    this.isInStock = updateItem.isInStock;
    this.isActive = updateItem.isActive;
  }

  public int getProductId() {
    return productId;
  }

  public String getProductCode() {
    return String.format("P%05d", productId);
  }
  public String getPriceString() {
    return String.format("%.2f", price);
  }

  public String getProductName() {
    return productName;
  }

  public String getProductDetail() {
    return productDetail;
  }

  public double getPrice() {
    return price;
  }

  public String getIsInStockText(){
    return (isInStock) ? "In Stock" : "Sold Out";
  }

  public String getIsStatsText() {
    return (isActive) ? "Active" : "InActive";
  }

  public boolean getIsActive() {
    return isActive;
  }

  public void setProductInfo(String productName, Double price){
    this.productName = productName;
    this.price = price;
  }

  public String objectToLineFormat(){
    return  productId + "," + productName + ","+
            productDetail + ","+ price + ","
            + ((isInStock) ? "true" : "false") + "," + ((isActive) ? "true" : "false")
            ;
  }

}
