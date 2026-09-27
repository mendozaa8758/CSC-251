/**
 * Base class for anything the store can sell.
 * StoreItem and Animal both extend this so the store can
 * treat them the same way when ringing up a sale.
 *
 * Contribution: Toni Bethune (Product / StoreItem / Animal class hierarchy)
 */
public abstract class Product {
    protected String name;
    protected double price;
    protected int quantityInStock;

    public Product(String name, double price, int quantityInStock) {
        this.name = name;
        this.price = price;
        this.quantityInStock = quantityInStock;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantityInStock() { return quantityInStock; }

    // Decision structure: don't let stock go negative
    public boolean sell(int amount) {
        if (amount <= 0 || amount > quantityInStock) {
            return false; // not enough stock or bad input
        }
        quantityInStock -= amount;
        return true;
    }

    public void restock(int amount) {
        if (amount > 0) {
            quantityInStock += amount;
        }
    }

    // Every subclass must describe itself for the GUI display
    public abstract String getDisplayInfo();
}
