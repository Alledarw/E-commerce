package order;

import MainObject.ShopManager;
import product.Product;
import stats.MainState;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderController {
  Scanner scan = new Scanner(System.in);

  ShopManager shopManager;
  public OrderController(ShopManager shopManager) {
    this.shopManager = shopManager;
  }

  public void menu() {
    if (!shopManager.isHasAdminPermission()) return;

    boolean run = true;
    while (run) {
      System.out.println("""
            
            ::::::::::::::::::: ORDER MENU :::::::::::::::::::
            1. All Orders
            2. Search
            3. Set Status of Order
            4. Delete Orders
            Q. Go back
            :::::::::::::::::::::::::::::::::::::::::::::::::::::::""");

      System.out.print("Input choice: ");
      // select menu
      String choice = scan.nextLine();
      System.out.println();

      // toUpperCase to check Q command
      switch (choice.toUpperCase()) {
        case "1" -> listAll();
        case "2" -> search();
        case "3" -> update();
        case "4" -> delete();
        case "Q" -> run = false; // quit while loop
        default -> System.out.println("Wrong command, Please try again");
      }
    }
  }

  // ++ Backend ++ ------------------------------------------------------------------------------

  public void listAll() {
    if (!shopManager.isHasBothPermission()) return;

    if (shopManager.getIsAdminLogin()) {
      // Print Order List
      printOrderList(shopManager.orders);
    } else {
      // Print only order of login customer.
      ArrayList<Order> orders = new ArrayList<>();
      for (int i = 0; i < shopManager.orders.size(); i++) {
        if ( shopManager.orders.get(i).getCustomerId() == shopManager.getLoginCustomerId())
          orders.add(shopManager.orders.get(i));
      }
      printOrderList(orders);
    }
  }

  private void search() {
    if (!shopManager.isHasAdminPermission()) return;

    if (shopManager.orders.isEmpty()) {
      System.out.println("Empty order!!");
      return;
    }

    showAllItems();

    System.out.println(
            """
                   
                    :::::::::::::::::: SEARCH ORDER :::::::::::::::::::
                    - Input a list number to search order
                    - Input Q to go back
                    :::::::::::::::::::::::::::::::::::::::::::::::::::::::"""
    );

    System.out.print("Input list Number: ");


    // select menu
    String inputString = scan.nextLine();
    System.out.println();

    int itemIndex = searchByInputNumber(inputString);
    if (itemIndex == -1){
      System.out.println("No item found.");
      return;
    }

    getOrderDetail(itemIndex);
  }


  private void update() {
    if (!shopManager.isHasAdminPermission()) return;

    if (shopManager.orders.isEmpty()) {
      System.out.println("Empty order!!");
      return;
    }

    while (true) {
      listAll();

      System.out.println(
              """
                      
                      :::::::::::::::::: SEARCH ORDER :::::::::::::::::::
                      - Input a list number to update status
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
      // Update item
      updateOrderItem(itemIndex);

    }

  }

  // Only pending order
  private void delete() {
    if (!shopManager.isHasAdminPermission()) return;

    if (shopManager.orders.isEmpty()) {
      System.out.println("Empty order!!");
      return;
    }

    while (true) {
      // Get only pending order
      ArrayList<Order> pendingOrders = new ArrayList<>();
      for (int i = 0; i < shopManager.orders.size(); i++) {
        if (shopManager.orders.get(i).getIsPending())
          pendingOrders.add(shopManager.orders.get(i));
      }
      printOrderList(pendingOrders);

      System.out.println(
              """
                      
                      :::::::::::::::::: SEARCH ORDER :::::::::::::::::::
                      - Input a list number to delete order
                      - Input Q to go back
                      :::::::::::::::::::::::::::::::::::::::::::::::::::::::"""
      );
      // select menu
      String inputString = scan.nextLine();
      System.out.println();

      if (inputString.equalsIgnoreCase("q")) break;

      // find only pending
      int pendingItemIndex;
      try {
        int choice = Integer.parseInt(inputString);
        pendingItemIndex = (choice < 0 || choice > (pendingOrders.size())) ? -1 : choice - 1;
      } catch (NumberFormatException ex) {
        // if user input string then return -1
        pendingItemIndex =  -1;
      }

      if (pendingItemIndex == -1){
        System.out.println("No item found.");
        return;
      }

      // get order pending info
      Order toDelete = pendingOrders.get(pendingItemIndex);
      // search to delete order by OrderId in orders and delete by index
      for (int i = 0; i < shopManager.orders.size(); i++) {
        if (shopManager.orders.get(i).getOrderId() == toDelete.getOrderId()) {
          // Delete order
          deleteOrder(i);
        }
      }
    }
  }

  public void deleteOrder(int itemIndex){
    Order selectedItem = shopManager.orders.get(itemIndex);
    ArrayList<Order> tempOrders = shopManager.orders;
    for (int i = 0; i < shopManager.orders.size(); i++) {
      if (shopManager.orders.get(i).getOrderId() == selectedItem.getOrderId()) {
        tempOrders.remove(i);
        break;
      }
    }
    shopManager.orders = tempOrders;

    // delete orderItem
    List<OrderItem> indicesToRemove = new ArrayList<>();
    for (int i = 0; i < shopManager.orderItems.size(); i++) {
      if (shopManager.orderItems.get(i).getOrderId() == selectedItem.getOrderId()) {
        indicesToRemove.add(shopManager.orderItems.get(i));
      }
    }

    // Remove elements in reverse order to avoid index issues
    for (int i = indicesToRemove.size() - 1; i >= 0; i--) {
      OrderItem toRemoveItem = indicesToRemove.get(i);

      for (int j = 0; j < shopManager.orderItems.size(); j++) {
        if (toRemoveItem.equals(shopManager.orderItems.get(i))){
          deleteOrderItem(i);
          break;
        }
      }
    }

    // Rewrite file because we remove an item form ArrayList
    rewriteFile();

    System.out.println("Updated item !!!");
  }

  // ++ Frontend & Backend ++ ------------------------------------------------------------------------------
  public void showAllItems(){
    if (!shopManager.isHasBothPermission()) return;

    if (shopManager.getIsAdminLogin()) {
      // Print Order List
      printOrderList(shopManager.orders);
    } else {
      // Print only order of login customer.
      ArrayList<Order> orders = new ArrayList<>();
      for (int i = 0; i < shopManager.orders.size(); i++) {
        if ( shopManager.orders.get(i).getCustomerId() == shopManager.getLoginCustomerId())
          orders.add(shopManager.orders.get(i));
      }
      printOrderList(orders);
    }
  }

  public void printOrderList(ArrayList<Order> orders){
    if (!shopManager.isHasBothPermission()) return;

    printDashLine(65);
    System.out.println(
            addWhiteSpace("No.",4) + "|"
                    + addWhiteSpace("Code" ,8) + "|"
                    + addWhiteSpace("Customer Name",22) + "|"
                    + addWhiteSpace("Purchase",10) + "|"
                    + addWhiteSpace("Status",10) + "|"
                    + addWhiteSpace("Order Date",22) + "|");
    printDashLine(65);

    if (orders.isEmpty()) {
      System.out.println("Empty order!!");
      return;
    }

    for (int i = 0; i < orders.size(); i++) {
      String customerName = getCustomerName(orders.get(i).getCustomerId());
      double totalPurchase = getTotalPurchase(orders.get(i).getOrderId());
      Order currentItem = orders.get(i);

      System.out.println(
              addWhiteSpace((i +1) + ". ",4) + "|"
                      + addWhiteSpace(currentItem.getOrderCode(),8) + "|"
                      + addWhiteSpace(customerName ,22) + "|"
                      + addWhiteSpace(totalPurchase + "",10) + "|"
                      + addWhiteSpace(currentItem.getIsPenddingString(),10) + "|"
                      + addWhiteSpace(currentItem.getOrderDate(),22)
      );

      // get Order detail
      //getOrderDetail(i);
    }
  }

  public void getOrderDetail(int orderIndexId){
    if (!shopManager.isHasBothPermission()) return;

    int selectedIndex = searchOrderByIndex(orderIndexId);
    if (selectedIndex == -1){
      System.out.println();
      return;
    }

    // Get Order Detail
    Order orderItem = shopManager.orders.get(selectedIndex);
    String customerName  = getCustomerName(orderItem.getCustomerId());
    printOrderDetail(customerName , orderItem);

    // List order item
    ArrayList<OrderItem> orderItems = getOrderItems(orderItem.getOrderId());
    listOrderItems(orderItems);
  }

  public void listOrderItems(ArrayList<OrderItem> orderItems) {
    printDashLine(75);
    System.out.println(
            addWhiteSpace("No.",4) + "|"
                    + addWhiteSpace("Product Name" ,30) + "|"
                    + addWhiteSpace("Amount",8) + "|"
                    + addWhiteSpace("Full Price",12) + "|"
                    + addWhiteSpace("Sale Price",12) + "|"
                    + addWhiteSpace("Total",12) + "|");
    printDashLine(75);

    if (orderItems.isEmpty()) {
      System.out.println("Empty order item!!");
    } else {
      for (int i = 0; i < orderItems.size(); i++) {
        // Get product Name
        String productName = getProductItemByProductId(orderItems.get(i).getProductId()).getProductName();
        OrderItem currentItem = orderItems.get(i);
        System.out.println(
                addWhiteSpace((i +1) + ". ",4) + "|"
                        + addWhiteSpace(productName ,30) + "|"
                        + addWhiteSpace(currentItem.getAmount()+"",8) + "|"
                        + addWhiteSpace(currentItem.getFullPriceString(),12) + "|"
                        + addWhiteSpace(currentItem.getSalePriceString(),12)
                        + addWhiteSpace(currentItem.getToPay() + "",12)
        );
      }
    }
    System.out.println("+".repeat(20) + " End of the list " + "+".repeat(20));
  }

  public String getCustomerName(int customerId){

    for (int i = 0; i < shopManager.customers.size(); i++) {
      if (shopManager.customers.get(i).getCustomerId() == customerId) {
        return shopManager.customers.get(i).getFullName();
      }
    }
    return "-";
  }

  // ++ Frontend ++ ------------------------------------------------------------------------------
  public boolean deleteOrderItem(int ItemIndex){
    if (!shopManager.isHasAdminPermission()) return false;

    // will return -1 if no item
    int selectedIndex = searchOrderItemByIndex(ItemIndex);

    if (selectedIndex == -1){
      return false;
    }
    // get member info before delete
    shopManager.orderItems.remove(selectedIndex);

    // Rewrite file because we remove an item form ArrayList
    rewriteOrderItemFile();

    //Success delete
    return true;
  }

  // ++ Help Method ++ ------------------------------------------------------------------------------

  public int searchByInputNumber(String inputString){
    try {
      int choice = Integer.parseInt(inputString);
      return (choice < 0 || choice > (shopManager.orders.size())) ? -1 : choice - 1;
    } catch (NumberFormatException ex) {
      // if user input string then return -1
      return  -1;
    }
  }

  public void updateOrderItem(int itemIndex){
    shopManager.orders.get(itemIndex).setPending();
    rewriteFile();

    System.out.println("Updated !!!");
  }

  public double getTotalPurchase(int orderId){
    double totalToPay = 0.0;
    for (int i = 0; i < shopManager.orderItems.size(); i++) {
      if (shopManager.orderItems.get(i).getOrderId() == orderId) {
        totalToPay = totalToPay + shopManager.orderItems.get(i).getToPay();
      }
    }
    return totalToPay;
  }

  public Product getProductItemByProductId(int productId){

    for (int i = 0; i < shopManager.products.size(); i++) {
      if (shopManager.products.get(i).getProductId() == productId) {
        return shopManager.products.get(i);
      }
    }
    return new Product();
  }

  public void rewriteOrderItemFile(){
    if (!shopManager.isHasBothPermission()) return;

    //set mainState to get filename
    shopManager.mainState = MainState.ORDER_ITEM;

    // Rewrite file because we remove an item form ArrayList
    String contentLines = "";
    ArrayList<OrderItem> tempOrderItem = new ArrayList<>();
    for (int i = 0; i < shopManager.orderItems.size(); i++) {
      contentLines = contentLines.concat(shopManager.orderItems.get(i).objectToLineFormat());
      if (i < shopManager.orderItems.size()-1) contentLines = contentLines.concat("\n");

      tempOrderItem.add(shopManager.orderItems.get(i));
    }

    shopManager.orderItems = tempOrderItem;
    shopManager.overwriteFile(contentLines);
  }

  public int searchOrderByIndex(int searchId){
    //search start from 0 ... n
    if (searchId < 0 || searchId > (shopManager.orders.size())) {
      return -1;
    }
    return searchId;
  }

  public void rewriteFile(){
    if (!shopManager.isHasBothPermission()) return;
    //set mainState to get filename
    shopManager.mainState = MainState.ORDER;
    // Rewrite file because we remove an item form ArrayList
    String contentLines = "";
    ArrayList<Order> tempOrder = new ArrayList<>();
    for (int i = 0; i < shopManager.orders.size(); i++) {
      contentLines = contentLines.concat(shopManager.orders.get(i).objectToLineFormat());
      if (i < shopManager.orders.size()-1) contentLines = contentLines.concat("\n");

      tempOrder.add(shopManager.orders.get(i));
    }

    shopManager.orders = tempOrder;
    shopManager.overwriteFile(contentLines);
  }

  private int searchOrderItemByIndex(int ItemIndex){
    //search start from 0 ... n
    if (ItemIndex < 0 || ItemIndex > (shopManager.products.size())) {
      return -1;
    }
    return ItemIndex;
  }

  public ArrayList<OrderItem> getOrderItems(int orderId){
    ArrayList<OrderItem> orderItems = new ArrayList<>();
    for (int i = 0; i < shopManager.orderItems.size(); i++) {
      if (shopManager.orderItems.get(i).getOrderId() == orderId) {
        orderItems.add(shopManager.orderItems.get(i));
      }
    }

    return orderItems;
  }

  // ++ View ++ ------------------------------------------------------------------------------

  public void printDeleteOrderItem(boolean isSuccess){
    if (isSuccess) {
      System.out.println("Deleted orderItem");
    } else {
      System.out.println("You can delete order Item only the order is pending.");
    }

  }


  public void printOrderDetail(String customerName,  Order currentItem) {
    printDashLine(70);

    System.out.println(
            addWhiteSpace("|",5)
                    + addWhiteSpace("Name",15) + ":"
                    + addWhiteSpace(customerName ,40)
                    + addWhiteSpace("",8) + "|");

    System.out.println(
            addWhiteSpace("|",5)
                    + addWhiteSpace("Status",15) + ":"
                    + addWhiteSpace(currentItem.getIsPendingText() ,40)
                    + addWhiteSpace("",8) + "|");

    System.out.println(
            addWhiteSpace("|",5)
                    + addWhiteSpace("Remark",15) + ":"
                    + addWhiteSpace(currentItem.getRemark() ,40)
                    + addWhiteSpace("",8) + "|");

    System.out.println(
            addWhiteSpace("|",5)
                    + addWhiteSpace("Order Date",15) + ":"
                    + addWhiteSpace(currentItem.getOrderDate() ,40)
                    + addWhiteSpace("",8) + "|");
    System.out.println(
            addWhiteSpace("|",5)
                    + addWhiteSpace("Approve Date",15) + ":"
                    + addWhiteSpace(currentItem.getCompleteDate() ,40)
                    + addWhiteSpace("",8) + "|");

    //printDashLine(70);
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


  // ++ Frontend ++ ------------------------------------------------------------------------------

  public void confirmOrder() {
    if (!shopManager.isHasCustomerPermission()) return;

    System.out.println("Special Needs, Like shipping, Order message, Gift, Ship to another adress");
    String remark = newItemInputForm("Requirement ");

    System.out.print("\n Confirm order? (y/n)  : ");

    String inputString = scan.nextLine();
    if (!inputString.equalsIgnoreCase("y")) {
      System.out.println("Cancel Process !!");
      return;
    }

    //set mainState to get filename
    shopManager.mainState = MainState.ORDER;
    // Add Order information
    int orderId = getNextOrderId();
    Order newItem = new Order(orderId, shopManager.getLoginCustomerId(), remark, true);
    shopManager.orders.add(newItem);
    shopManager.addNewLine(newItem.objectToLineFormat());

    // save tempOrderItems to orderItem
    for (int i = 0; i < shopManager.tempOrderItems.size(); i++) {
      //update temporary orderId to real orderId
      shopManager.tempOrderItems.get(i).setOrderId(orderId);
      //add order item
      //- add new line to OrderItem.text.
      OrderItem newOrderItem = shopManager.tempOrderItems.get(i);

      //set mainState to get filename
      shopManager.mainState = MainState.ORDER_ITEM;

      int orderItemId = getNextOrderItemId();
      newOrderItem.setOrderItemId(orderItemId);

      shopManager.orderItems.add(newOrderItem);
      shopManager.addNewLine(newOrderItem.objectToLineFormat());
    }

    // clear temp order
    shopManager.tempOrderItems = new ArrayList<>();
    // Print order confirmed
    System.out.println("Order confirmed!!");
  }

  public int getNextOrderId(){
    if (shopManager.orders.isEmpty()) return 1;

    int maxProductId = -1;

    for (Order order : shopManager.orders) {
      if (order.getOrderId() > maxProductId) {
        maxProductId = order.getOrderId();
      }
    }
    maxProductId++;
    return maxProductId;
  }

  public int getNextOrderItemId(){
    if (shopManager.orderItems.isEmpty()) return 1;

    int maxProductId = -1;

    for (OrderItem orderItem : shopManager.orderItems) {
      if (orderItem.getOrderItemId() > maxProductId) {
        maxProductId = orderItem.getOrderItemId();
      }
    }
    maxProductId++;
    return maxProductId;
  }

  public void orderHistory(int customerId){
    if (!shopManager.isHasBothPermission()) return;

    ArrayList<Order> orderHistory = new ArrayList<>();
    // get only order of login customer
    for (int i = 0; i < shopManager.orders.size(); i++) {
      if (shopManager.orders.get(i).getCustomerId() == customerId) {
        orderHistory.add(shopManager.orders.get(i));
      }
    }
    // Print Order List
    printOrderList(orderHistory);
  }


  public String newItemInputForm(String label){
    System.out.print(label + " : ");
    return scan.nextLine();
  }

}
