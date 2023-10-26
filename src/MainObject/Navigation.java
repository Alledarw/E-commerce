package MainObject;

import customer.Customer;
import customer.CustomerController;
import order.OrderController;
import order.OrderItem;
import product.Product;
import product.ProductController;

import java.util.ArrayList;
import java.util.Scanner;

public class Navigation {
  Scanner scan = new Scanner(System.in);
  ShopManager shopManager;
  CustomerController customerController;
  ProductController productController;
  OrderController orderController;

  int maxTry = 3;
  public Navigation(ShopManager shopManager) {
    this.shopManager = shopManager;
    customerController = new CustomerController(shopManager);
    productController = new ProductController(shopManager);
    orderController = new OrderController(shopManager);
  }

  //:::::::::::::::::::::::: Main Menu ::::::::::::::::::::::::
  public void mainMenu(){
    // get data from text files

    boolean run = true;
    while (run) {
      System.out.println("""
            
            ::::::::::::::::::: Main Menu :::::::::::::::::::
            1. Backend
            2. Frontend
            Q. Exit Program
            :::::::::::::::::::::::::::::::::::::::::::::::::::::::""");
      System.out.print("Input choice: ");
      // select menu
      String choice = scan.nextLine();
      System.out.println();

      // toUpperCase to check Q command
      switch (choice.toUpperCase()) {
        case "1" -> {
          shopManager.setIsAdmin(true);
          doLogin();
          if (shopManager.getIsAdminLogin() && shopManager.getIsAdmin()) {
            this.backendMenu();
          }
        }
        case "2" -> {
          shopManager.setIsAdmin(false);
          this.frontendMenu();
        }
        case "Q" -> {
          System.out.println("Exit Program :)");
          run = false; // quit while loop
        }
        default -> System.out.println("Input a number or Q to exit program");
      }
    }

  }


  //:::::::::::::::::::::::: Backend Menu ::::::::::::::::::::::::
  public void backendMenu(){
    if (!shopManager.isHasAdminPermission()) return;

    boolean run = true;
    while (run) {
      if (!shopManager.getIsAdminLogin()) run = false;

      System.out.println("""
            
            ::::::::::::::::::: BACKEND MENU :::::::::::::::::::
            1. Manage Products
            2. Manage Customers
            3. Manage Orders
            4. Logout
            Q. Go back
            :::::::::::::::::::::::::::::::::::::::::::::::::::::::""");

      System.out.print("Input choice: ");

      // select menu
      String choice = scan.nextLine();
      System.out.println();

      // toUpperCase to check Q command
      switch (choice.toUpperCase()) {
        case "1" -> manageProduct();
        case "2" -> manageCustomer();
        case "3" -> manageOrder();
        case "4" -> {
          setLogout();
          run = false;
        }
        case "Q" -> {
          run = false; // quit while loop
        }
        default -> System.out.println("Wrong command, Please try again");
      }
    }
  }

  private void manageProduct(){
    if (!shopManager.isHasAdminPermission()) return;
    productController.menu();
  }
  private void manageCustomer(){
    if (!shopManager.isHasAdminPermission()) return;
    customerController.menu();
  }
  private void manageOrder(){
    if (!shopManager.isHasAdminPermission()) return;
    orderController =  new OrderController(shopManager);
    orderController.menu();
  }


  //:::::::::::::::::::::::: Frontend Menu ::::::::::::::::::::::::
  public void frontendMenu(){
    boolean run = true;
    while (run) {
      boolean isCustomerLogin = shopManager.getIsCustomerLogin();
      if (isCustomerLogin) {
        System.out.println("""
            
            ::::::::::::::::::: FRONTEND MENU :::::::::::::::::::
            1. View product
            2. Order history
            3. View Profile
            4. Logout
            Q. Go back
            :::::::::::::::::::::::::::::::::::::::::::::::::::::::""");
      } else {
        System.out.println("""
            
            ::::::::::::::::::: FRONTEND MENU :::::::::::::::::::
            1. View product
            2. Order history
            3. Login / Register
            Q. Go back
            :::::::::::::::::::::::::::::::::::::::::::::::::::::::""");
      }

      System.out.print("Input choice: ");


      // select menu
      String choice = scan.nextLine();

      // toUpperCase to check Q command
      switch (choice.toUpperCase()) {
        case "1" -> viewProduct();
        case "2" -> getOrderHistory();
        case "3" -> {
          // check text in printFrontendMenu()
          if (!shopManager.getIsCustomerLogin()) {
            authenticationMenu();
          } else {
            customerController.viewProfileByCustomerId(shopManager.getLoginCustomerId());
          }
        }
        case "4" -> setLogout();
        case "Q" ->  run = false;
        default -> System.out.println("Wrong command, Please try again");
      }

    }
  }

  private void viewProduct(){
    // get only active product
    ArrayList<Product> activeProducts = productController.getActiveProduct();
    productController.showProductList(activeProducts);

    boolean run = true;
    while (run) {
      printSelectToCartMenu(shopManager.tempOrderItems);
      // select menu
      String choice = scan.nextLine();
      System.out.println();

      // toUpperCase to check Q command
      switch (choice.toUpperCase()) {
        case "1" -> productController.showProductList(activeProducts);
        case "2" -> productController.addProductToCart();
        case "3" -> productController.viewCart();
        case "4" -> {
          if (shopManager.tempOrderItems.isEmpty())
            System.out.println("Wrong command, Please try again");
          else  productController.deleteOrderItem();
        }
        case "5" -> {
          if (shopManager.tempOrderItems.isEmpty())
            System.out.println("Wrong command, Please try again");
          else  orderController.confirmOrder();
        }
        case "Q" ->  run = false;
        default -> System.out.println("Wrong command, Please try again");
      }
    }
  }

  private void getOrderHistory(){
    if (!shopManager.isHasCustomerPermission()) {
      doLogin();
      return;
    }


    while (true) {
      orderController.orderHistory(shopManager.getLoginCustomerId());

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

      if (inputString.equalsIgnoreCase("q")) break;
      int itemIndex = orderController.searchByInputNumber(inputString);

      if (itemIndex == -1) {
        System.out.println("No item found.");
        return;
      }
      orderController.getOrderDetail(itemIndex);
    }

  }

  // ------------------
  public void doLogin(){
    int countTry = 0;
    String username;
    String password;

    while (true) {
      // check max try
      if (maxTry == countTry) {
        countTry = 0;

        System.out.println("\nYou have tried " + maxTry + " times.");
        System.out.println("Q : Go back to main or enter to try again.");
        String inputChoice = scan.nextLine();

        if (inputChoice.equalsIgnoreCase("Q")) break;
      }

      this.getHeader();
      // input username/password
      System.out.print("Username: ");
      username = scan.nextLine();

      System.out.print("Password: ");
      password = scan.nextLine();

      //check if correct
      boolean isSuccess;
      if (shopManager.getIsAdmin()) {
        isSuccess = isAdminLoginSuccess(username, password);
      } else {
        isSuccess = isCustomerLoginSuccess(username,password);
      }

      if (isSuccess) {
        setLogin();
        break;
      } else {
        countTry++;
        System.out.println(" Wrong username or password !!! you have "
                +(maxTry - countTry) + " left.");
      }

    }
  }

  private void getHeader(){
    if (shopManager.getIsAdmin()) {
      System.out.println("\n::::: LOGIN TO BACKEND :::: ");
    }
    else {
      if (!shopManager.getTempOrderItems().isEmpty()) {
        System.out.println("\n::::: LOGIN TO CONFIRM ORDER :::: ");
      } else {
        System.out.println("\n::::: LOGIN TO FRONTEND :::: ");
      }
    }
  }

  public void setLogin() {

    if (shopManager.getIsAdmin()) {
      shopManager.setAdminLogin(true);
    } else {
      shopManager.setCustomerLogin(true);
    }
  }

  public void setLogout(){
    if (shopManager.getIsAdmin()) {
      shopManager.setAdminLogin(false);
    } else {
      shopManager.setLoginCustomerId(0);
      shopManager.setCustomerLogin(false);
    }

    shopManager.setIsAdmin(false);
  }

  private boolean isCustomerLoginSuccess(String username, String password){

    for (Customer customer : shopManager.customers) {
      if (customer.getUsername().equals(username) && customer.getPassword().equals(password)) {
        shopManager.setLoginCustomerId(customer.getCustomerId());
        return true;
      }
    }
    return false;
  }

  private boolean isAdminLoginSuccess(String username, String password){
    for (Admin admin : shopManager.admins) {
      if (admin.getUsername().equals(username) && admin.getPassword().equals(password))
        return true;
    }
    return false;
  }


  public void authenticationMenu(){
    boolean run = true;
    while (run) {
      System.out.println("""
            ::::::::::::::::::: CUSTOMER MENU :::::::::::::::::::
            Note: You need to login before you can confirm order
                  or view order history
            
            1. You already have an account
            2. Register
            Q. Go back
            :::::::::::::::::::::::::::::::::::::::::::::::::::::::""");
      System.out.print("Input choice: ");

      // select menu
      String choice = scan.nextLine();

      // toUpperCase to check Q command
      switch (choice.toUpperCase()) {
        case "1" -> {
          doLogin();
        }
        case "2" -> {
          customerController = new CustomerController(shopManager);
          customerController.register();
        }
        case "Q" ->  run = false;
        default -> System.out.println("Wrong command, Please try again");
      }

      // exit loop if login success
      if (shopManager.getIsCustomerLogin()) run = false;

    }
  }


  public void printSelectToCartMenu(ArrayList<OrderItem> tempOrderItem) {
    if (tempOrderItem.isEmpty()) {
      System.out.println("""
            
            ::::::::::::::::::: SHOPPING MENU :::::::::::::::::::
            1. All Products
            2. Add product to cart
            3. View cart
            Q. Go back
            :::::::::::::::::::::::::::::::::::::::::::::::::::::::""");
    } else {
      System.out.println("""
            
            ::::::::::::::::::: SHOPPING MENU :::::::::::::::::::
            1. All Products
            2. Add product to cart
            3. View cart
            4. Delete order item
            5. Confirm order
            Q. Go back
            :::::::::::::::::::::::::::::::::::::::::::::::::::::::""");
    }


    System.out.print("Input choice: ");
  }
}
