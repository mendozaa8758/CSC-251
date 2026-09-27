/**
 * A regular store product, e.g. bag of feed, leash, water bottle.
 *
 * Contribution: Toni Bethune (Product / StoreItem / Animal class hierarchy)
 */
public class StoreItem extends Product {
    private String category; // e.g. "Feed", "Supplies", "Toys"

    public StoreItem(String name, double price, int quantityInStock, String category) {
        super(name, price, quantityInStock);
        this.category = category;
    }

    public String getCategory() { return category; }

    @Override
    public String getDisplayInfo() {
        return String.format("[Item] %s (%s) - $%.2f - Qty: %d",
                name, category, price, quantityInStock);
    }
}
