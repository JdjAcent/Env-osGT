import java.util.Locale;

public abstract class Shipment {
    private String code;
    private String senderName;
    private String recipientName;
    private double weight;
    private DestinationType destinationType;
    private ShipmentStatus status;
    private int deliveryAttempts;

    protected Shipment(String code, String senderName, String recipientName,
                       double weight, DestinationType destinationType) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío.");
        }
        if (senderName == null || senderName.trim().isEmpty()
                || recipientName == null || recipientName.trim().isEmpty()) {
            throw new IllegalArgumentException("Remitente y destinatario son obligatorios.");
        }
        if (!Double.isFinite(weight) || weight <= 0) {
            throw new IllegalArgumentException("El peso debe ser positivo y finito.");
        }
        if (destinationType == null) {
            throw new IllegalArgumentException("El destino es obligatorio.");
        }
        this.code = code.trim();
        this.senderName = senderName.trim();
        this.recipientName = recipientName.trim();
        this.weight = weight;
        this.destinationType = destinationType;
        this.status = ShipmentStatus.IN_WAREHOUSE;
        this.deliveryAttempts = 0;
    }

    public String getCode() { return code; }
    public String getSenderName() { return senderName; }
    public String getRecipientName() { return recipientName; }
    public double getWeight() { return weight; }
    public DestinationType getDestinationType() { return destinationType; }
    public ShipmentStatus getStatus() { return status; }
    public int getDeliveryAttempts() { return deliveryAttempts; }

    public abstract double calculateCost();
    public abstract int getEstimatedDays();
    public abstract int getMaxAttempts();

    public boolean requiresRefrigeration() { return false; }

    public boolean isCompatibleWith(Vehicle vehicle) {
        return vehicle != null && (!requiresRefrigeration() || vehicle.hasRefrigeration());
    }

    public void assign() {
        if (status != ShipmentStatus.IN_WAREHOUSE) {
            throw new IllegalStateException("El envío debe estar en bodega.");
        }
        status = ShipmentStatus.ASSIGNED;
    }

    public void unassign() {
        if (status != ShipmentStatus.ASSIGNED) {
            throw new IllegalStateException("El envío debe estar asignado.");
        }
        status = ShipmentStatus.IN_WAREHOUSE;
    }

    public boolean dispatch() {
        if (status != ShipmentStatus.ASSIGNED) {
            throw new IllegalStateException("El envío debe estar asignado.");
        }
        boolean retry = deliveryAttempts > 0;
        status = ShipmentStatus.IN_ROUTE;
        return retry;
    }

    public void registerAttempt(boolean success) {
        if (status != ShipmentStatus.IN_ROUTE) {
            throw new IllegalStateException("El envío debe estar en ruta.");
        }
        deliveryAttempts++;
        if (success) {
            status = ShipmentStatus.DELIVERED;
        } else if (deliveryAttempts >= getMaxAttempts()) {
            status = ShipmentStatus.RETURNED;
        } else {
            status = ShipmentStatus.IN_WAREHOUSE;
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "%s | %s | %s -> %s | %.2f kg | %s | %s | Intentos: %d/%d",
                code, getClass().getSimpleName(), senderName, recipientName,
                weight, destinationType, status, deliveryAttempts, getMaxAttempts());
    }
}
