import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * Entry point. Simple JOptionPane-driven menu so the whole flow
 * is easy to demo and easy to expand into a full Swing GUI later.
 *
 * Contribution: Donnesha Jamison (menu flow, wiring CSV load/save into the app)
 */
public class FarmStoreApp {

    private static final String INVENTORY_FILE = "inventory.csv";
    private static final String SERVICES_FILE = "services.csv";

    public static void main(String[] args) {
        Store store = new Store();
        loadData(store); // load from CSV, or seed + create the files on first run

        boolean running = true;
        while (running) {
            String choice = JOptionPane.showInputDialog(null,
                    "Aunt & Uncle's Farm Store\n\n" +
                    "1. View Inventory\n" +
                    "2. Sell a Product\n" +
                    "3. Add New Item/Animal\n" +
                    "4. Schedule a Service\n" +
                    "5. View Services\n" +
                    "6. Collect Payment for Service\n" +
                    "7. View Total Revenue\n" +
                    "8. Save Data to File\n" +
                    "9. Mark a Service Completed\n" +
                    "10. Restock a Product\n" +
                    "0. Exit\n\n" +
                    "Enter choice:");

            if (choice == null) break; // user hit Cancel

            switch (choice.trim()) {
                case "1":
                    JOptionPane.showMessageDialog(null, store.buildInventoryReport());
                    break;

                case "2":
                    sellProductFlow(store);
                    break;

                case "3":
                    addProductFlow(store);
                    break;

                case "4":
                    scheduleServiceFlow(store);
                    break;

                case "5":
                    JOptionPane.showMessageDialog(null, store.buildServiceReport());
                    break;

                case "6":
                    collectPaymentFlow(store);
                    break;

                case "7":
                    JOptionPane.showMessageDialog(null,
                            "Total Revenue: $" + String.format("%.2f", store.getTotalRevenue()));
                    break;

                case "8":
                    saveData(store);
                    JOptionPane.showMessageDialog(null, "Data saved.");
                    break;

                case "9":
                    markServiceCompletedFlow(store);
                    break;

                case "10":
                    restockProductFlow(store);
                    break;

                case "0":
                    running = false;
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice, try again.");
            }
        }

        // save automatically on the way out too, in case they forgot to hit option 8
        saveData(store);
        JOptionPane.showMessageDialog(null, "Thanks for stopping by the farm store!");
    }

    private static void sellProductFlow(Store store) {
        String name = JOptionPane.showInputDialog("Product name to sell:");
        if (name == null) return;
        String amountStr = JOptionPane.showInputDialog("Quantity:");
        if (amountStr == null) return;

        try {
            int amount = Integer.parseInt(amountStr.trim());
            String result = store.sellProduct(name, amount);
            JOptionPane.showMessageDialog(null, result);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid whole number.");
        }
    }

    private static void addProductFlow(Store store) {
        String type = JOptionPane.showInputDialog("Add (1) Store Item or (2) Animal?");
        if (type == null) return;

        if (type.trim().equals("1")) {
            String name = JOptionPane.showInputDialog("Item name:");
            String category = JOptionPane.showInputDialog("Category (e.g. Feed, Supplies):");
            double price = promptDouble("Price:");
            int qty = promptInt("Starting quantity:");
            store.addProduct(new StoreItem(name, price, qty, category));
            JOptionPane.showMessageDialog(null, "Item added.");

        } else if (type.trim().equals("2")) {
            String name = JOptionPane.showInputDialog("Animal name/label:");
            String species = JOptionPane.showInputDialog("Species (e.g. Duck, Hamster):");
            String breed = JOptionPane.showInputDialog("Breed:");
            double price = promptDouble("Price:");
            int qty = promptInt("Quantity:");

            String breeder = JOptionPane.showInputDialog(
                    "If from a local breeder, enter breeder name (leave blank if farm-raised):");

            Animal a = (breeder != null && !breeder.trim().isEmpty())
                    ? new Animal(name, price, qty, species, breed, breeder.trim())
                    : new Animal(name, price, qty, species, breed);

            store.addProduct(a);
            JOptionPane.showMessageDialog(null, "Animal added.");
        } else {
            JOptionPane.showMessageDialog(null, "Invalid selection.");
        }
    }

    private static void scheduleServiceFlow(Store store) {
        String serviceType = JOptionPane.showInputDialog("Service type (e.g. Basic Checkup, Vaccination):");
        if (serviceType == null) return;
        String customer = JOptionPane.showInputDialog("Customer name:");
        String animalDesc = JOptionPane.showInputDialog("Animal description (species/name):");
        String date = JOptionPane.showInputDialog("Scheduled date (e.g. 2026-09-20):");
        double cost = promptDouble("Cost of service:");

        store.scheduleService(new Service(serviceType, customer, animalDesc, date, cost));
        JOptionPane.showMessageDialog(null, "Service scheduled.");
    }

    private static void collectPaymentFlow(Store store) {
        JOptionPane.showMessageDialog(null, store.buildServiceReport());
        int index = promptInt("Enter the service number to mark paid (1 = first listed):") - 1;
        store.collectPayment(index);
        JOptionPane.showMessageDialog(null, "Payment processed (if service was completed).");
    }

    private static void markServiceCompletedFlow(Store store) {
        JOptionPane.showMessageDialog(null, store.buildServiceReport());
        int index = promptInt("Enter the service number to mark completed (1 = first listed):") - 1;
        store.markServiceCompleted(index);
        JOptionPane.showMessageDialog(null, "Service marked completed (if a valid number was entered).");
    }

    private static void restockProductFlow(Store store) {
        String name = JOptionPane.showInputDialog("Product name to restock:");
        if (name == null) return;
        int amount = promptInt("Amount to add to stock:");
        JOptionPane.showMessageDialog(null, store.restockProduct(name, amount));
    }

    // --- small input helpers with basic validation loops ---
    private static double promptDouble(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(message);
            try {
                return Double.parseDouble(input.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Enter a valid number.");
            }
        }
    }

    private static int promptInt(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(message);
            try {
                return Integer.parseInt(input.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Enter a valid whole number.");
            }
        }
    }

    private static void seedSampleData(Store store) {
        store.addProduct(new StoreItem("Chicken Feed 20lb", 14.99, 25, "Feed"));
        store.addProduct(new StoreItem("Rabbit Hutch", 89.99, 4, "Supplies"));
        store.addProduct(new Animal("Duckling", 8.00, 15, "Duck", "Pekin"));
        store.addProduct(new Animal("Hamster", 12.50, 6, "Hamster", "Syrian", "Miller's Small Animal Farm"));
    }

    // --- CSV load/save wrappers ---
    private static void loadData(Store store) {
        try {
            store.loadInventoryFromFile(INVENTORY_FILE);
            store.loadServicesFromFile(SERVICES_FILE);
        } catch (IOException e) {
            // no CSV files yet (first run) - start from sample data and write the files
            seedSampleData(store);
            saveData(store);
        }
    }

    private static void saveData(Store store) {
        try {
            store.saveInventoryToFile(INVENTORY_FILE);
            store.saveServicesToFile(SERVICES_FILE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving data: " + e.getMessage());
        }
    }
}
