import java.util.Locale;

public class Vehicle {
    private String plate;
    private double capacity;
    private boolean refrigeration;

    public Vehicle(String plate, double capacity, boolean refrigeration) {
        if (plate == null || plate.trim().isEmpty()) {
            throw new IllegalArgumentException("La placa no puede estar vacía.");
        }
        if (!Double.isFinite(capacity) || capacity <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser positiva y finita.");
        }
        this.plate = plate.trim().toUpperCase(Locale.ROOT);
        this.capacity = capacity;
        this.refrigeration = refrigeration;
    }

    public String getPlate() { return plate; }
    public double getCapacity() { return capacity; }
    public boolean hasRefrigeration() { return refrigeration; }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s | Capacidad: %.2f kg | Refrigeración: %s",
                plate, capacity, refrigeration ? "Sí" : "No");
    }
}
