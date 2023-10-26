package MainObject;

import customer.Customer;
import order.Order;
import order.OrderItem;
import product.Product;
import service.FileService;
import stats.FileState;
import stats.MainState;

import java.util.ArrayList;

public class ShopManager {
  public ShopManager() {
    // get all data to ArrayList when program loaded
    readAllFiles();
  }

  //---------------- FileService Control
  FileService fileService;
  public MainState mainState;  // which file we want to read/write
  FileState fileState; // which action to do :  read, add new line or overwrite
  String writeToFileText; // content line(s) you want to add new line or overwrite
  // Read text file and will store in ArrayList
  public ArrayList<Admin> admins = new ArrayList<>();
  public ArrayList<Customer> customers = new ArrayList<>();
  public ArrayList<Product> products = new ArrayList<>();
  public ArrayList<Order> orders = new ArrayList<>();
  public ArrayList<OrderItem> orderItems = new ArrayList<>();
  public ArrayList<OrderItem> tempOrderItems = new ArrayList<>();
  //---------------- Permission Control
  boolean isAdmin; // isAdmin=true : backend, isAdmin=false : frontend
  boolean isAdminLogin = false; // true : login to admin
  boolean isCustomerLogin = false; // true : login to customer
  int loginCustomerId = 0; // customerId -> to order and view profile

  //Call this function after register
  public void loginForNewCustomer(int customerId){
    isCustomerLogin = true;
    setLoginCustomerId(customerId);
  }

  // if has admin admin permission isAdminLogin = true
  public boolean isHasAdminPermission(){
    if (!getIsAdminLogin())  System.out.println("You don't have permission");
    return getIsAdminLogin();
  }

  // has customer permission  isCustomerLogin = true
  public boolean isHasCustomerPermission(){
    if (!getIsCustomerLogin())  System.out.println("You don't have permission");
    return getIsCustomerLogin();
  }

  // isCustomerLogin or isAdminLogin
  public boolean isHasBothPermission(){
    boolean isHasPermission = (getIsCustomerLogin() || getIsAdminLogin());
    if (!isHasPermission)  System.out.println("You don't have permission");
    return isHasPermission;
  }

  //---------------- Read/Write file control state
  public MainState getMainState(){
    return mainState;
  }
  public void setMainState(MainState state) {
    mainState = state;
  }

  //----------------- Manage File Methods
  public FileState getFileState(){
    return fileState;
  }
  public String getWriteToFileText(){
    return writeToFileText;
  }

  //----------------- Add new item to data object
  public void addNewAdmin(Admin newItem) {
    this.admins.add(newItem);
  }

  // Get newItem from FileManager->addNewCustomer
  public void addNewCustomer(Customer newItem) {
    this.customers.add(newItem);
  }
  // Get newItem from FileManager->addNewProduct
  public void addNewProduct(Product newItem) {
    this.products.add(newItem);
  }
  // Get newItem from FileManager->addNewOrder
  public void addNewOrder(Order newItem) {
    this.orders.add(newItem);
  }
  // Get newItem from FileManager->addNewOrderItem
  public void addNewOrderItem(OrderItem newItem) {
    this.orderItems.add(newItem);
  }

  // Read all files when program loaded
  public void readAllFiles(){
    //ADMIN,PRODUCT,CUSTOMER,ORDER,ORDERITEM,
    for (MainState mainStateItem : MainState.values()) {
      mainState = mainStateItem;
      fileState = FileState.READ;
      fileService = new FileService(this, mainState, fileState);
      fileService.choose();
    }
  }

  // write a new line to text file (call from controller when add new item)
  public void addNewLine(String contentLine){
    this.writeToFileText = contentLine;
    this.fileState = FileState.NEW_LINE;
    fileService.choose();
  }

  // overwrite text file (call from controller when delete or update)
  public void overwriteFile(String contentLines){
    this.writeToFileText = contentLines;
    //Write file
    this.fileState = FileState.OVERWRITE;
    fileService.choose();
    //
  }

  //----------------- getter and setter
  public boolean getIsAdmin() {
    return isAdmin;
  }

  public boolean getIsAdminLogin() {
    return isAdminLogin;
  }

  public boolean getIsCustomerLogin() {
    return isCustomerLogin;
  }

  public int getLoginCustomerId() {
    return loginCustomerId;
  }

  public ArrayList<OrderItem> getTempOrderItems() {
    return tempOrderItems;
  }

  public void setIsAdmin(boolean newValue) {
    isAdmin = newValue;
  }

  public void setAdminLogin(boolean newValue) {
    isAdminLogin = newValue;
  }

  public void setCustomerLogin(boolean newValue) {
    isCustomerLogin = newValue;
  }

  public void setLoginCustomerId(int customerId){
    loginCustomerId = customerId;
  }
}