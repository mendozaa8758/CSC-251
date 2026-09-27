import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Central manager class. Holds all inventory and services,
 * and contains the business logic methods the GUI will call.
 *
 * Contribution: Sean Hendry (Store business logic + CSV file read/write)
 */
public class Store {
    private List<Product> inventory;   // holds both StoreItems and Animals (polymorphism)
    private List<Service> services;
    private double totalRevenue;

    public Store() {
        inventory = new ArrayList<>();
        services = new ArrayList<>();
        totalRevenue = 0.0;
    }

    // --- Inventory management ---
    public void addProduct(Product p) {
        inventory.add(p);
    }

    // Restock an existing item or animal by name; returns a result message for the GUI
    public String restockProduct(String name, int amount) {
        for (Product p : inventory) {
            if (p.getName().equalsIgnoreCase(name)) {
                p.restock(amount);
                return "Restocked " + amount + " x " + name;
            }
        }
        return "Product not found: " + name;
    }

    // Sell an item or animal by name; returns a result message for the GUI
    public String sellProduct(String name, int amount) {
        for (Product p : inventory) {
            if (p.getName().equalsIgnoreCase(name)) {
                if (p.sell(amount)) {
                    double total = p.getPrice() * amount;
                    totalRevenue += total;
                    return "Sold " + amount + " x " + name + " for $" + String.format("%.2f", total);
                } else {
                    return "Sale failed: not enough stock of " + name;
                }
            }
        }
        return "Product not found: " + name;
    }

    // --- Services ---
    public void scheduleService(Service s) {
        services.add(s);
    }

    // Mark a scheduled service as completed, so it becomes eligible for payment
    public void markServiceCompleted(int serviceIndex) {
        if (serviceIndex >= 0 && serviceIndex < services.size()) {
            services.get(serviceIndex).markCompleted();
        }
    }

    public void collectPayment(int serviceIndex) {
        if (serviceIndex >= 0 && serviceIndex < services.size()) {
            Service s = services.get(serviceIndex);
            if (s.markPaid()) {
                totalRevenue += s.getCost();
            }
        }
    }

    public double getTotalRevenue() { return totalRevenue; }

    // --- Reporting helper for the GUI ---
    public String buildInventoryReport() {
        StringBuilder sb = new StringBuilder();
        for (Product p : inventory) {
            sb.append(p.getDisplayInfo()).append("\n");
        }
        return sb.length() == 0 ? "No inventory yet." : sb.toString();
    }

    public String buildServiceReport() {
        StringBuilder sb = new StringBuilder();
        for (Service s : services) {
            sb.append(s.getStatusSummary()).append("\n");
        }
        return sb.length() == 0 ? "No services scheduled." : sb.toString();
    }

    // --- CSV file I/O: inventory ---
    // Row format: type,name,price,quantityInStock,field1,field2,field3,field4
    // ITEM rows use field1 for category; ANIMAL rows use field1-4 for
    // species/breed/isFromBreeder/breederName.
    public void saveInventoryToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("type,name,price,quantityInStock,field1,field2,field3,field4");
            for (Product p : inventory) {
                if (p instanceof Animal) {
                    Animal a = (Animal) p;
                    writer.println(String.join(",",
                            "ANIMAL",
                            CsvUtil.escape(a.getName()),
                            String.valueOf(a.getPrice()),
                            String.valueOf(a.getQuantityInStock()),
                            CsvUtil.escape(a.getSpecies()),
                            CsvUtil.escape(a.getBreed()),
                            String.valueOf(a.isFromBreeder()),
                            CsvUtil.escape(a.getBreederName())));
                } else if (p instanceof StoreItem) {
                    StoreItem item = (StoreItem) p;
                    writer.println(String.join(",",
                            "ITEM",
                            CsvUtil.escape(item.getName()),
                            String.valueOf(item.getPrice()),
                            String.valueOf(item.getQuantityInStock()),
                            CsvUtil.escape(item.getCategory()),
                            "", "", ""));
                }
            }
        }
    }

    public void loadInventoryFromFile(String filename) throws IOException {
        List<Product> loaded = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // skip header row
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<String> f = CsvUtil.parseLine(line);
                String type = f.get(0);
                String name = f.get(1);
                double price = Double.parseDouble(f.get(2));
                int qty = Integer.parseInt(f.get(3));

                if (type.equals("ANIMAL")) {
                    String species = f.get(4);
                    String breed = f.get(5);
                    boolean fromBreeder = Boolean.parseBoolean(f.get(6));
                    String breederName = f.get(7);
                    Animal a = fromBreeder
                            ? new Animal(name, price, qty, species, breed, breederName)
                            : new Animal(name, price, qty, species, breed);
                    loaded.add(a);
                } else if (type.equals("ITEM")) {
                    String category = f.get(4);
                    loaded.add(new StoreItem(name, price, qty, category));
                }
            }
        }
        inventory = loaded;
    }

    // --- CSV file I/O: services ---
    // Row format: serviceType,customerName,animalDescription,scheduledDate,cost,completed,paid
    public void saveServicesToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("serviceType,customerName,animalDescription,scheduledDate,cost,completed,paid");
            for (Service s : services) {
                writer.println(String.join(",",
                        CsvUtil.escape(s.getServiceType()),
                        CsvUtil.escape(s.getCustomerName()),
                        CsvUtil.escape(s.getAnimalDescription()),
                        CsvUtil.escape(s.getScheduledDate()),
                        String.valueOf(s.getCost()),
                        String.valueOf(s.isCompleted()),
                        String.valueOf(s.isPaid())));
            }
        }
    }

    public void loadServicesFromFile(String filename) throws IOException {
        List<Service> loaded = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // skip header row
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<String> f = CsvUtil.parseLine(line);
                String serviceType = f.get(0);
                String customer = f.get(1);
                String animalDesc = f.get(2);
                String date = f.get(3);
                double cost = Double.parseDouble(f.get(4));
                boolean completed = Boolean.parseBoolean(f.get(5));
                boolean paid = Boolean.parseBoolean(f.get(6));

                Service s = new Service(serviceType, customer, animalDesc, date, cost);
                if (completed) {
                    s.setCompleted(true);
                }
                if (paid) {
                    s.setPaidStatus(true);
                }
                loaded.add(s);
            }
        }
        services = loaded;
    }
}
