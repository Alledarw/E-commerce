package customer;

import MainObject.ShopManager;
import order.OrderController;
import stats.MainState;

import java.util.Objects;
import java.util.Scanner;

public class CustomerController {
  Scanner scan = new Scanner(System.in);
  ShopManager shopManager;
  OrderController orderController;
  public CustomerController(ShopManager shopManager) {
    this.shopManager = shopManager;
    this.orderController = new OrderController(shopManager);
  }

  public void menu() {
    if (!shopManager.isHasAdminPermission()) return;

    boolean run = true;
    while (run) {
      System.out.println("""
            
            ::::::::::::::::::: CUSTOMER MENU :::::::::::::::::::
            1. All customer
            2. View Profile
            3. Update Customer
            4. Delete customer
            Q. Go back
            :::::::::::::::::::::::::::::::::::::::::::::::::::::::""");

      System.out.print("Input choice: ");

      // select menu
      String inputString = scan.nextLine();
      System.out.println();

      // Switch-case : Style 1
//      switch (inputString.toUpperCase()) {
//        case "1" : listAll();
//        case "2" : search();
//          ......
//        default: System.out.println("Wrong command, Please try again");
//      }

      // Switch-case : Style 2
      // toUpperCase to check Q command
      switch (inputString.toUpperCase()) {
        case "1" -> listAll();
        case "2" -> search();
        case "3" -> updateStatus();
        case "4" -> delete();
        case "Q" -> run = false; // quit while loop
        default -> System.out.println("Wrong command, Please try again");
      }
    }
  }

  // ++ Backend ++ ------------------------------------------------------------------------------
  private void listAll() {
    // Print Header
    printDashLine(130);
    System.out.println(
            addWhiteSpace("No.",4) + "|"
                    + addWhiteSpace("Code" ,7) + "|"
                    + addWhiteSpace("Username",12) + "|"
                    + addWhiteSpace("Full Name",22) + "|"
                    + addWhiteSpace("Email",22) + "|"
                    + addWhiteSpace("Address",22) + "|"
                    + addWhiteSpace("ZipCode",7) + "|"
                    + addWhiteSpace("Country",15) + "|"
                    + addWhiteSpace("Active",8)
    );
    printDashLine(130);

    // exit method if it has no customers
    if (shopManager.customers.isEmpty()) {
      System.out.println("No item found.");
      return;
    }

    // show customer list
    for (int i = 0; i < shopManager.customers.size(); i++) {
      Customer currentItem = shopManager.customers.get(i);
      System.out.println(
              addWhiteSpace((i +1) + ". ",4) + "|"
                      + addWhiteSpace(currentItem.getCustomerCode() ,7) + "|"
                      + addWhiteSpace(currentItem.getUsername(),12) + "|"
                      + addWhiteSpace(currentItem.getFullName(),22) + "|"
                      + addWhiteSpace(currentItem.getEmail(),22) + "|"
                      + addWhiteSpace(currentItem.getAddress(),22) + "|"
                      + addWhiteSpace(currentItem.getZipCode(),7) + "|"
                      + addWhiteSpace(currentItem.getCountry(),15) + "|"
                      + addWhiteSpace(currentItem.getIsActiveString(),8)
      );
    }
    // print footer
    System.out.println("+".repeat(20) + " End of the list " + "+".repeat(20));
  }

  private void search() {
    if (!shopManager.isHasAdminPermission()) return;

    // exit method if it no customers
    if (shopManager.customers.isEmpty()) {
      System.out.println("Empty customer!!");
      return;
    }

    while (true) {
      listAll();
      // input a list number  1...n
      System.out.print(" input index list : ");
      // select menu
      String inputString = scan.nextLine();
      System.out.println();

      // if q then exit method
      if (inputString.equalsIgnoreCase("q")) break;

      // get the index item 0...n-1
      int itemIndex = searchByInputNumber(inputString);
      viewProfileByItemIndex(itemIndex);
    }
  }

  private void viewProfileByItemIndex(int searchIndex){
    if (!shopManager.isHasBothPermission()) return;
    // get the index item -1...(n -1)
    int itemIndex = searchByItemIndex(searchIndex);

    // if it -1 then show not found
    if (itemIndex == -1){
      System.out.println("No item found.");
      return;
    }

    // Confirm update
    printDetail(shopManager.customers.get(itemIndex));
    System.out.print("\n Do you want to update? (y/n)  : ");

    String inputString = scan.nextLine();
    // if input <> y then exit method
    if (!inputString.equalsIgnoreCase("y")) {
      System.out.println("Cancel Process !!");
      return;
    }

    //show update form
    updateForm(itemIndex);
  }


  private void viewCustomerOrder(){
    if (!shopManager.isHasAdminPermission()) return;

    while (true) {
      orderController.listAll();

      System.out.println(
              """

                       :::::::::::::::::: VIEW ORDER MENU :::::::::::::::::::
                       1. Input index (No.) to view order
                       Q. Go back
                       :::::::::::::::::::::::::::::::::::::::::::::::::::::::\
                      """);

      System.out.print("Input choice: ");

      // get number of list item 1...n
      String inputString = scan.nextLine();
      System.out.println();

      if (inputString.equalsIgnoreCase("q")) break;

      // get the index item 0...(n-1)
      int itemIndex = searchByInputNumber(inputString);
      Customer selectedItem = shopManager.customers.get(itemIndex);

      //Show history
      orderController.orderHistory(selectedItem.getCustomerId());
    }
  }

  private void updateStatus(){
    if (!shopManager.isHasAdminPermission()) return;

    // if empty then exit method
    if (shopManager.customers.isEmpty()) {
      System.out.println("Empty customer!!");
      return;
    }

    while (true) {
      listAll();
      System.out.println(
              """

                       :::::::::::::::::: VIEW PROFILE MENU :::::::::::::::::::
                       1. Input index (No.) to update customer status
                       Q. Go back
                       :::::::::::::::::::::::::::::::::::::::::::::::::::::::\
                      """);
      System.out.print("Input list Number: ");

      // get list number 1...n
      String inputString = scan.nextLine();
      System.out.println();

      // if q then exit method
      if (inputString.equalsIgnoreCase("q")) break;

      // get the index item 0...n-1
      int itemIndex = searchByInputNumber(inputString);

      // update customer
      updateStatus(itemIndex);
      System.out.println("Updated!!");
    }
  }

  private void delete() {
    if (!shopManager.isHasAdminPermission()) return;

    // exit method if empty customers
    if (shopManager.customers.isEmpty()) {
      System.out.println("Empty customer!!");
      return;
    }

    while (true) {
      listAll();

      System.out.println(
              """

                       :::::::::::::::::: DELETE CUSTOMER :::::::::::::::::::
                       1. Input index (No.) to delete
                       Q. Go back
                       :::::::::::::::::::::::::::::::::::::::::::::::::::::::\
                      """);
      // get list number  1...n
      String inputString = scan.nextLine();
      System.out.println();

      if (inputString.equalsIgnoreCase("q")) break;

      // get the index item 0...n-1
      int itemIndex = searchByInputNumber(inputString);
      // delete item
      deleteCustomer(itemIndex);
    }
  }

  public void updateStatus(int itemIndex){
    // customer data by index
    Customer selectedItem = shopManager.customers.get(itemIndex);
    //set status :  check in Customer.java
    selectedItem.setActive();
    // Overwrite file
    rewriteFile();
  }

  public void deleteCustomer(int itemIndex){
    Customer selectedItem = shopManager.customers.get(itemIndex);

    // ignore if customer has ordered products
    // delete only customer who have never ordered
    if (!checkIfHasOrder(selectedItem.getCustomerId())) {
      // remove item for ArrayList
      shopManager.customers.remove(itemIndex);
      // Overwrite file
      rewriteFile();
      System.out.println("Deleted !!!");
    } else {
      System.out.println("Fail delete because customer has some orders !!!");
    }
  }

  // ++ Frontend & Backend ++ ------------------------------------------------------------------------------
  public void updateForm(int indexItem){
    if (!shopManager.isHasBothPermission()) return;
    // get customer by index
    Customer selectedItem = shopManager.customers.get(indexItem);
    // get customer update form -> input data -> return customer item
    Customer updateItem = updateProfile(selectedItem);
    // confirm update
    System.out.print("\n Confirm to update? (y/n)  : ");
    String inputString = scan.nextLine();
    // if input <> y then exit method
    if (!inputString.equalsIgnoreCase("y")) {
      System.out.println("Cancel Process !!");
      return;
    }

    // Update Customer
    selectedItem.updateItem(updateItem);
    System.out.println("Updated !!!");

    // Overwrite file
    rewriteFile();
  }

  public Customer updateProfile(Customer selectedItem){
    System.out.println("::::: Update Customer ::::: ");
    System.out.println("-- Enter to skip edit --  ");
    String firstname = updateItemInputForm(selectedItem.getFirstname(),"firstname");
    String lastname = updateItemInputForm(selectedItem.getLastname(),"lastname");
    String username = selectedItem.getUsername(); //updateItemInputForm(selectedItem.getUsername(),"username");
    String password = updateItemInputForm(selectedItem.getPassword(),"password");
    String email = selectedItem.getEmail(); //updateItemInputForm(selectedItem.getEmail(),"email");
    String address = updateItemInputForm(selectedItem.getAddress(),"address");
    String zipCode = updateItemInputForm(selectedItem.getZipCode(),"zipCode");
    String country = updateItemInputForm(selectedItem.getCountry(),"country");
    return  new Customer(selectedItem.getCustomerId(),firstname,lastname,username,
            password,email,address,zipCode,country,selectedItem.getIsActive()
    );
  }



  // ++ Frontend ++ ------------------------------------------------------------------------------

  public void register(){
    // show form input
    Customer newItem = newItemForm();

    // check username
    if (checkIfUsernameDuplicated(newItem.getUsername())) {
      System.out.println("\n\nUser name " + newItem.getUsername() + " has registered!!\n\n");
      return;
    }

    // check email
    if (checkIfEmailDuplicated(newItem.getEmail())) {
      System.out.println("\n\nEmail " + newItem.getEmail() + " has registered!!\n\n");
      return;
    }

    //Confirm
    System.out.print("\n Confirm to register? (y/n)  : ");
    String inputString = scan.nextLine();

    if (!inputString.equalsIgnoreCase("y")) {
      System.out.println("Cancel Process !!");
      return;
    }

    // add new Item
    shopManager.mainState = MainState.CUSTOMER; // Customer.text
    String contentLine = newItem.objectToLineFormat();
    shopManager.customers.add(newItem);
    shopManager.addNewLine(contentLine);

    // Set customer
    shopManager.loginForNewCustomer(newItem.getCustomerId());

    System.out.println("Added !!!");
  }

  public Customer newItemForm(){
    System.out.println("::::: Register Customer ::::: ");
    String firstname = newItemInputForm("firstname");
    String lastname = newItemInputForm("lastname");
    String username = newItemInputForm("username");
    String password = newItemInputForm("password");
    String email = newItemInputForm("email");
    String address = newItemInputForm("address");
    String zipCode = newItemInputForm("zipCode");
    String country = newItemInputForm("country");

    int customerId = getNextId();
    return  new Customer(customerId,firstname,lastname,username,
            password,email,address,zipCode,country,false
    );
  }

  // ++ Help Method ++ ------------------------------------------------------------------------------

  public void rewriteFile() {
    //set mainState to get filename
    shopManager.mainState = MainState.CUSTOMER; // Customer.tex
    // Rewrite file because we remove an item form ArrayList
    String contentLines = "";
    for (int i = 0; i < shopManager.customers.size(); i++) {
      // convert ArrayList to String
      //ex : 1,Customer,Gottlieb,customer,customer,waleerat@gmail.com,-,-,-,true
      contentLines = contentLines.concat(shopManager.customers.get(i).objectToLineFormat());
      if (i < shopManager.customers.size()-1) contentLines = contentLines.concat("\n");
    }
    shopManager.overwriteFile(contentLines);
  }

  public int searchByItemIndex(int itemIndex){
    //search start from 0 ... n-1
    if (itemIndex < 0 || itemIndex > (shopManager.customers.size())) {
      return -1;
    }
    return itemIndex;
  }

  public boolean checkIfUsernameDuplicated(String username) {
    for (int i = 0; i < shopManager.customers.size(); i++) {
      if (Objects.equals(shopManager.customers.get(i).getUsername(), username))
        return true;
    }
    return false;
  }

  public boolean checkIfEmailDuplicated(String email) {
    for (int i = 0; i < shopManager.customers.size(); i++) {
      if (Objects.equals(shopManager.customers.get(i).getEmail(), email))
        return true;
    }
    return false;
  }

  public boolean checkIfHasOrder(int customerId) {
    for (int i = 0; i < shopManager.orders.size(); i++) {
      if ( shopManager.orders.get(i).getCustomerId() == customerId)
        return true;
    }
    return false;
  }

  public int getNextId(){
    if (shopManager.customers.isEmpty()) return 1;

    int maxProductId = 0;
    for (Customer customer : shopManager.customers) {
      if (customer.getCustomerId() > maxProductId) {
        // Update maxId if a larger id is found
        maxProductId = customer.getCustomerId();
      }
    }
    maxProductId++;
    return maxProductId;
  }

  public int getCustomerIndexItemById(int customerId){
    // customerId : 1...n
    for (int i = 0; i < shopManager.customers.size(); i++) {
      if (shopManager.customers.get(i).getCustomerId() == customerId) {
        return i;
      }
    }
    return -1;
  }

  public int searchByInputNumber(String inputString){
    try {
      int choice = Integer.parseInt(inputString);
      //Style 1
      return (choice < 0 || choice > (shopManager.customers.size())) ? -1 : choice - 1;
      //Style 2
//      if (choice < 0 || choice > (shopManager.customers.size())) return -1;
//      else return choice-1;
      //Style 3
//      if (choice < 0 || choice > (shopManager.customers.size())) {
//        return -1;
//      }
//      else {
//        return choice - 1;
//      }
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

  // ++ View ++ ------------------------------------------------------------------------------

  public String addWhiteSpace(String text, int maxAmount){
    if(text.length() > maxAmount){
      return text.substring(0, maxAmount - 3) + "...";
    }
    return text + " ".repeat(maxAmount - text.length());
  }

  public void printDashLine(int length){
    System.out.println("-".repeat(length));
  }


  public void printDetail(Customer currentItem){
    printDashLine(70);
    System.out.println("|"+ " ".repeat(22)
            + "CUSTOMER PROFILE " + currentItem.getCustomerCode()
            + " ". repeat(22)+ "|");
    printDashLine(70);

    System.out.println(
            addWhiteSpace("|",5)
                    + addWhiteSpace("Code",15) + ": "
                    + addWhiteSpace(currentItem.getCustomerCode() ,39)
                    + addWhiteSpace("",8) + "|");
    System.out.println(
            addWhiteSpace("|",5)
                    + addWhiteSpace("Name",15) + ": "
                    + addWhiteSpace(currentItem.getFullName() ,39)
                    + addWhiteSpace("",8) + "|");

    System.out.println(
            addWhiteSpace("|",5)
                    + addWhiteSpace("Username",15) + ": "
                    + addWhiteSpace(currentItem.getUsername() ,39)
                    + addWhiteSpace("",8) + "|");

    System.out.println(
            addWhiteSpace("|",5)
                    + addWhiteSpace("Email",15) + ": "
                    + addWhiteSpace(currentItem.getEmail() ,39)
                    + addWhiteSpace("",8) + "|");

    System.out.println(
            addWhiteSpace("|",5)
                    + addWhiteSpace("Address",15) + ": "
                    + addWhiteSpace(currentItem.getAddress() ,39)
                    + addWhiteSpace("",8) + "|");

    System.out.println(
            addWhiteSpace("|",5)
                    + addWhiteSpace("Zipcode",15) + ": "
                    + addWhiteSpace(currentItem.getZipCode() ,39)
                    + addWhiteSpace("",8) + "|");

    System.out.println(
            addWhiteSpace("|",5)
                    + addWhiteSpace("Country",15) + ": "
                    + addWhiteSpace(currentItem.getCountry() ,39)
                    + addWhiteSpace("",8) + "|");
    printDashLine(70);
  }

  // frontend
  public void viewProfileByCustomerId(int customerId) {
    if (!shopManager.isHasBothPermission()) return;

    int itemIndex = getCustomerIndexItemById(customerId);

    if (itemIndex == -1){
      System.out.println("No item found.");
      return;
    }
    // View Profile
    showProfile(itemIndex);
  }

  public void showProfile(int itemIndex){
    if (!shopManager.isHasBothPermission()) return;

    printDetail(shopManager.customers.get(itemIndex));
    System.out.print("\n Do you want to update? (y/n)  : ");

    String inputString = scan.nextLine();
    if (!inputString.equalsIgnoreCase("y")) {
      System.out.println("No item found.");
      return;
    }

    updateForm(itemIndex);
  }
}
