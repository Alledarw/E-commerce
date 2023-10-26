import MainObject.Navigation;
import product.ProductController;
import MainObject.ShopManager;

public class Main {
  public static void main(String[] args) {
    ShopManager shopManager = new ShopManager();
    Navigation navigation = new Navigation(shopManager);
    navigation.mainMenu();
  }
}