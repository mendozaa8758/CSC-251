/**
 * An animal available for sale (ducks, chickens, hamsters, rabbits, etc.)
 * Supports the "specialty resale from local breeders" requirement via
 * the isFromBreeder / breederName fields.
 *
 * Contribution: Toni Bethune (Product / StoreItem / Animal class hierarchy)
 */
public class Animal extends Product {
    private String species;   // e.g. "Duck", "Hamster"
    private String breed;     // e.g. "Pekin", "Syrian"
    private boolean isFromBreeder;
    private String breederName; // empty if raised on the farm

    public Animal(String name, double price, int quantityInStock,
                  String species, String breed) {
        super(name, price, quantityInStock);
        this.species = species;
        this.breed = breed;
        this.isFromBreeder = false;
        this.breederName = "";
    }

    // Overloaded constructor for breeder-consigned animals
    public Animal(String name, double price, int quantityInStock,
                  String species, String breed, String breederName) {
        this(name, price, quantityInStock, species, breed);
        this.isFromBreeder = true;
        this.breederName = breederName;
    }

    public String getSpecies() { return species; }
    public String getBreed() { return breed; }
    public boolean isFromBreeder() { return isFromBreeder; }
    public String getBreederName() { return breederName; }

    @Override
    public String getDisplayInfo() {
        String source = isFromBreeder ? (" - Breeder: " + breederName) : " - Farm-raised";
        return String.format("[Animal] %s (%s/%s) - $%.2f - Qty: %d%s",
                name, species, breed, price, quantityInStock, source);
    }
}
