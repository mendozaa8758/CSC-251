/**
 * A scheduled service, e.g. basic vet check, nail trim, vaccination.
 * Tracks whether it has been completed and whether payment was received.
 *
 * Contribution: Aiden Canady (Service class + CsvUtil helper)
 */
public class Service {
    private String serviceType;
    private String customerName;
    private String animalDescription;
    private String scheduledDate; // kept as String for simplicity (e.g. "2026-09-20")
    private double cost;
    private boolean completed;
    private boolean paid;

    public Service(String serviceType, String customerName, String animalDescription,
                   String scheduledDate, double cost) {
        this.serviceType = serviceType;
        this.customerName = customerName;
        this.animalDescription = animalDescription;
        this.scheduledDate = scheduledDate;
        this.cost = cost;
        this.completed = false;
        this.paid = false;
    }

    public void markCompleted() { completed = true; }

    // Decision structure: can't pay for a service that hasn't happened
    // (business rule your group can adjust)
    public boolean markPaid() {
        if (!completed) {
            return false;
        }
        paid = true;
        return true;
    }

    public String getStatusSummary() {
        String status = completed ? "Completed" : "Scheduled";
        String payStatus = paid ? "Paid" : "Unpaid";
        return String.format("%s for %s on %s | %s - %s - $%.2f",
                serviceType, customerName, scheduledDate, status, payStatus, cost);
    }

    public double getCost() { return cost; }
    public boolean isPaid() { return paid; }
    public String getServiceType() { return serviceType; }
    public String getCustomerName() { return customerName; }
    public String getAnimalDescription() { return animalDescription; }
    public String getScheduledDate() { return scheduledDate; }
    public boolean isCompleted() { return completed; }

    // These bypass the markPaid()/markCompleted() business rule on purpose:
    // they exist only so the CSV loader can restore a saved status without
    // re-triggering the "must be completed before paid" check.
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setPaidStatus(boolean paid) { this.paid = paid; }
}
