package product;

import MainObject.ShopManager;
import order.OrderController;
import order.OrderItem;
import stats.MainState;

import java.util.ArrayList;
import java.util.Scanner;

public class ProductController {
  Scanner scan = new Scanner(System.in);

  ShopManager shopManager;
  OrderController orderController;
  public ProductController(ShopManager shopManager) {
    this.shopManager = shopManager;
    this.orderController = new OrderController(shopManager);
  }

  public void menu() {
    if (!shopManager.isHasAdminPermission()) return;

    boolean run = true;
    while (run) {
      System.out.println("""
            
            ::::::::::::::::::: PRODUCT MENU :::::::::::::::::::
            1. All Products
            2. New Product
            3. Edit Product
            4. Delete
            Q. Go back
            :::::::::::::::::::::::::::::::::::::::::::::::::::::::""");
      System.out.print("Input choice: ");
      // select menu
      String choice = scan.nextLine();
      System.out.println();

      // toUpperCase to check Q command
      switch (choice.toUpperCase()) {
        case "1" -> listAll();
        case "2" -> newItem();
        case "3" -> update();
        case "4" -> delete();
        case "Q" -> run = false; // quit while loop
        default -> System.out.println("Wrong command, Please try again");
      }
    }
  }

  // ++ Backend ++ ------------------------------------------------------------------------------

  private void listAll() {

    printDashLine(107);
    System.out.println(
            addWhiteSpace("No.",4) + "|"
                    + addWhiteSpace("Code" ,8) + "|"
                    + addWhiteSpace("Product Name" ,30) + "|"
                    + addWhiteSpace("Product Detail" ,30) + "|"
                    + addWhiteSpace("Price",10) + "|"
                    + addWhiteSpace("Stock",10) + "|"
                    + addWhiteSpace("Status",10) + "|");
    printDashLine(107);


    if (shopManager.products.isEmpty()) {
      System.out.println("Empty product!!");
      return;
    }

    for (int i = 0; i < shopManager.products.size(); i++) {
      Product currentItem = shopManager.products.get(i);
      System.out.println(
              addWhiteSpace((i +1) + ". ",4) + "|"
                      + addWhiteSpace(currentItem.getProductCode() ,8) + "|"
                      + addWhiteSpace(currentItem.getProductName(),30) + "|"
                      + addWhiteSpace(currentItem.getProductDetail(),30) + "|"
                      + addWhiteSpace(currentItem.getPriceString(),10) + "|"
                      + addWhiteSpace(currentItem.getIsInStockText(),10)
                      + addWhiteSpace(currentItem.getIsStatsText(),10)
      );
    }
    System.out.println("+".repeat(20) + " End of the list " + "+".repeat(20));
  }


  private void newItem() {
    if (!shopManager.isHasAdminPermission()) return;
    //set mainState to get filename
    shopManager.mainState = MainState.PRODUCT;
    //Add new item
    System.out.println("::::: New Product ::::: \n");
    String productName = newItemInputForm("Product Name");
    String productDetail = newItemInputForm("Product Detail");
    double price;
    do {
      String priceString = newItemInputForm("Price");
      try {
        price = Double.parseDouble(priceString);
        break;
      } catch (NumberFormatException e) {
        System.out.println("Price have to be number!! \n");
      }

    } while(true);

    int productId = getNextId();
    Product newItem = new Product(productId, productName, productDetail, price);

    // optional
    System.out.print("\n Confirm to add? (y/n)  : ");
    String inputString1 = scan.nextLine();

    if (!inputString1.equalsIgnoreCase("y")) {
      System.out.println("Cancel Process !!");
      return;
    }

    String contentLine = newItem.objectToLineFormat();
    shopManager.products.add(newItem);
    shopManager.addNewLine(contentLine);

    System.out.println("Updated !!!");
    System.out.println(" >> " + newItem.getProductName());

  }

  private void update() {
    if (!shopManager.isHasAdminPermission()) return;

    while (true) {
      listAll();

      System.out.println(
              """
                      :::::::::::::::::: ADD TO CART :::::::::::::::::::
                      - Input a list number to update
                      - Input Q to go back
                      :::::::::::::::::::::::::::::::::::::::::::::::::::::::"""
      );

      System.out.print("Input list Number: ");

      // select menu
      String inputString = scan.nextLine();
      System.out.println();

      if (inputString.equalsIgnoreCase("q")) break;

      int itemIndex = searchByInputNumber(inputString);

      if (itemIndex == -1){
        System.out.println("No item found.");
        return;
      }

      Product selectedItem = shopManager.products.get(itemIndex);

      System.out.println("::::: Update Product ::::: \n");
      System.out.println("-- Enter to skip edit --  \n");
      String productName = updateItemInputForm(selectedItem.getProductName(),"Product Name");
      String productDetail = updateItemInputForm(selectedItem.getProductName(),"Product Detail");

      double price;
      do {
        String priceString = updateItemInputForm(selectedItem.getPriceString(),"price");

        try {
          price = Double.parseDouble(priceString);
          break;
        } catch (NumberFormatException e) {
          System.out.println("Price have to be number!! \n");
        }

      } while(true);

      //Set Instock
      System.out.println("Input 1=In Stock, 2=SoldOut \n");
      String inStockChoice = updateItemInputForm(selectedItem.getIsInStockText(),"In Stock");
      boolean isInStock = inStockChoice.equals("1");

      //Set Active
      System.out.println("Input 1=Active, 2=InActive \n");
      String inActiveChoice = updateItemInputForm(selectedItem.getIsInStockText(),"Active");
      boolean isActive = inStockChoice.equals("1");

      Product updateItem = new Product(selectedItem.getProductId(), productName, productDetail, price, isInStock, isActive);

      // Confirm Update
      System.out.print("\n Confirm to update? (y/n)  : ");
      String inputString1 = scan.nextLine();

      if (!inputString1.equalsIgnoreCase("y")) {
        System.out.println("Cancel Process !!");
        return;
      }

      // Do update product
      selectedItem.updateItem(updateItem);
      // Overwrite file
      rewriteFile();

      System.out.println("Updated !!!");
      System.out.println(" >> " + updateItem.getProductName());

    }
  }

  private void delete() {
    if (!shopManager.isHasAdminPermission()) return;

    if (shopManager.products.isEmpty()) {
      System.out.println("Empty product!!");
      return;
    }

    while (true) {
      listAll();

      System.out.println(
              """
                      :::::::::::::::::: ADD TO CART :::::::::::::::::::
                      - Input a list number to update
                      - Input Q to go back
                      :::::::::::::::::::::::::::::::::::::::::::::::::::::::"""
      );

      System.out.print("Input list Number: ");

      // select menu
      String inputString = scan.nextLine();
      System.out.println();

      if (inputString.equalsIgnoreCase("q")) break;

      int itemIndex = searchByInputNumber(inputString);

      if (itemIndex == -1){
        System.out.println("No item found.");
        return;
      }

      // Confirm Update
      System.out.print("\n Confirm to update? (y/n)  : ");
      String inputString1 = scan.nextLine();

      if (!inputString1.equalsIgnoreCase("y")) {
        System.out.println("Cancel Process !!");
        return;
      }

      // remove Item
      removeProductItem(itemIndex);
      System.out.println("Removed !!!");

    }

  }

  public void removeProductItem(int itemIndex) {
    Product selectedItem = shopManager.products.get(itemIndex);
    // ignore if customer has ordered
    if (checkIfHasOrder(selectedItem.getProductId())) {
      System.out.println("Fail delete because customer has some orders !!!");
      return;
    }

    // remove Item
    shopManager.products.remove(itemIndex);
    // Rewrite file because we remove an item form ArrayList
    rewriteFile();

    System.out.println("Deleted !!!");
    System.out.println(" >> " + selectedItem.getProductName());
  }


  // ++ Frontend ++ ------------------------------------------------------------------------------

  public void addProductToCart() {

    while (true) {
      ArrayList<Product> activeProducts = getActiveProduct();
      showProductList(activeProducts);

      System.out.println(
              """
                    
                      :::::::::::::::::: ADD TO CART :::::::::::::::::::
                      - Input a list number to add to cart
                      - Input Q to go back
                      :::::::::::::::::::::::::::::::::::::::::::::::::::::::"""
      );

      System.out.print("Input list Number: ");
      // select menu
      String inputString = scan.nextLine();
      System.out.println();

      if (inputString.equalsIgnoreCase("q")) break;

      int activeItemIndex = searchByInputNumber(inputString);
      if (activeItemIndex == -1) {
        System.out.println("No item found.");
        return;
      }
      //Add to cart
      addToCart(activeItemIndex);
    }

  }

  /* user without login can add product to card (save in TempOrderItem)*/
  private void addToCart(int activeItemIndex){
    boolean isNewItem = true;
    // get only active products
    ArrayList<Product> activeProducts = getActiveProduct();
    Product selectedItem = activeProducts.get(activeItemIndex);

    // If product has ordered then amount+1
    for (int i = 0; i < shopManager.tempOrderItems.size(); i++) {
      if (shopManager.tempOrderItems.get(i).getProductId() == selectedItem.getProductId()){
        // amount + 1 if already added to cart
        int amount = shopManager.tempOrderItems.get(i).getAmount() + 1;
        shopManager.tempOrderItems.get(i).setAmount(amount);
        isNewItem = false;
        break;
      }
    }

    // product item is not in tempOrderItem then add new item
    if (isNewItem) {
      // Add to cart if new product
      int amount = 1;
      shopManager.tempOrderItems.add(new OrderItem(0,0, selectedItem.getProductId(), amount,
              selectedItem.getPrice(), selectedItem.getPrice()
      ));

      System.out.println(selectedItem.getProductName() + " added to cart");
    }
  }

  public void viewCart() {
    // List selected product items (order items)
    orderController.listOrderItems(shopManager.tempOrderItems);
  }
  public void deleteOrderItem() {

    while (true) {
      viewCart();
      System.out.println(
              """
                     
                      :::::::::::::::::: ADD TO CART :::::::::::::::::::
                      - Input a list number to delete from cart
                      - Input Q to go back
                      :::::::::::::::::::::::::::::::::::::::::::::::::::::::"""
      );

      System.out.print("Input list Number: ");

      // select menu
      String inputString = scan.nextLine();
      System.out.println();
      if (inputString.equalsIgnoreCase("q")) break;

      int itemIndex = -1;
      try {
        int choice = Integer.parseInt(inputString);
        // if choice is not in range then return -1
        if (choice > 0 || choice < (shopManager.tempOrderItems.size())) {
          itemIndex = choice-1;
        }
      } catch (NumberFormatException ex) {
        System.out.print("Input a number");
        return;
      }

      if (itemIndex == -1) {
        System.out.println("No item found.");
        return;
      }

      // remove item
      removeTempOrderItem(itemIndex);

    }
  }

  public void removeTempOrderItem(int itemIndex){
    // remove item
    OrderItem selectedItem = shopManager.tempOrderItems.get(itemIndex);
    shopManager.tempOrderItems.remove(itemIndex);

    // get deleted product to show which order item has deleted (optional)
    for (int i = 0; i < shopManager.products.size(); i++) {
      if (shopManager.products.get(i).getProductId() == selectedItem.getProductId()) {
        System.out.println(shopManager.products.get(i) + " deleted from cart");
        break;
      }
    }
  }

  public void showProductList(ArrayList<Product> products){
    printDashLine(97);
    System.out.println(
            addWhiteSpace("No.",4) + "|"
                    + addWhiteSpace("Code" ,8) + "|"
                    + addWhiteSpace("Product Name" ,30) + "|"
                    + addWhiteSpace("Product Detail" ,30) + "|"
                    + addWhiteSpace("Price",10) + "|"
                    + addWhiteSpace("Status",10) + "|");
    printDashLine(97);

    for (int i = 0; i < products.size(); i++) {
      Product currentItem = products.get(i);
      System.out.println(
              addWhiteSpace((i +1) + ". ",4) + "|"
                      + addWhiteSpace(currentItem.getProductCode() ,8) + "|"
                      + addWhiteSpace(currentItem.getProductName(),30) + "|"
                      + addWhiteSpace(currentItem.getProductDetail(),30) + "|"
                      + addWhiteSpace(currentItem.getPriceString(),10) + "|"
                      + addWhiteSpace(currentItem.getIsInStockText(),10)
      );
    }
    System.out.println("+".repeat(20) + " End of the list " + "+".repeat(20));
  }

// ++ Help Method ++ ------------------------------------------------------------------------------
public ArrayList<Product> getActiveProduct(){
  ArrayList<Product> products = new ArrayList<>();
  for (int i = 0; i < shopManager.products.size(); i++) {
    if (shopManager.products.get(i).getIsActive())
      products.add(shopManager.products.get(i));
  }
  return products;
}


  public boolean checkIfHasOrder(int productId) {
    for (int i = 0; i < shopManager.orderItems.size(); i++) {
      if ( shopManager.orderItems.get(i).getProductId() == productId)
        return true;
    }
    return false;
  }

  public int getNextId(){
    if (shopManager.products.isEmpty()) return 1;

    int maxProductId = -1;

    for (Product product : shopManager.products) {
      if (product.getProductId() > maxProductId) {
        maxProductId = product.getProductId();
      }
    }
    maxProductId++;
    return maxProductId;
  }

  public void rewriteFile() {
    if (!shopManager.isHasAdminPermission()) return;

    //set mainState to get filename
    shopManager.mainState = MainState.PRODUCT;

    // Rewrite file because we remove an item form ArrayList
    String contentLines = "";
    for (int i = 0; i < shopManager.products.size(); i++) {
      contentLines = contentLines.concat(shopManager.products.get(i).objectToLineFormat());
      if (i < shopManager.products.size()-1) contentLines = contentLines.concat("\n");
    }
    shopManager.overwriteFile(contentLines);
  }

  public int searchByInputNumber(String inputNumber){
    try {
      int choice = Integer.parseInt(inputNumber);
      return (choice < 0 || choice > (shopManager.products.size())) ? -1 : choice - 1;
    } catch (NumberFormatException ex) {
      // if user input string then return -1
      return  -1;
    }
  }

  public String newItemInputForm(String label){
    System.out.print(label + " : ");
    return scan.nextLine();
  }
  public String updateItemInputForm(String oldString, String label) {
    System.out.print(label + " : " + oldString + "  > ");
    String inputString = scan.nextLine();

    return (inputString.isEmpty()) ? oldString : inputString;
  }

  public String addWhiteSpace(String text, int maxAmount){
    if(text.length() > maxAmount){
      return text.substring(0, maxAmount - 3) + "...";
    }
    return text + " ".repeat(maxAmount - text.length());
  }

  public void printDashLine(int length){
    System.out.println("-".repeat(length));
  }

}
