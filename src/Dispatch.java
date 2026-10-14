import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Locale;

public class Dispatch {
    private int id;
    private Vehicle vehicle;
    private List<Shipment> shipments;
    private Set<String> pending;
    private DispatchStatus status;

    public Dispatch(int id, Vehicle vehicle) {
        if (id <= 0 || vehicle == null) {
            throw new IllegalArgumentException("Identificador o vehículo inválido.");
        }
        this.id = id;
        this.vehicle = vehicle;
        this.shipments = new ArrayList<>();
        this.pending = new LinkedHashSet<>();
        this.status = DispatchStatus.PREPARING;
    }

    public int getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public List<Shipment> getShipments() { return Collections.unmodifiableList(shipments); }
    public Set<String> getPending() { return Collections.unmodifiableSet(pending); }
    public DispatchStatus getStatus() { return status; }

    public double getLoadedWeight() {
        double total = 0;
        for (Shipment shipment : shipments) {
            total += shipment.getWeight();
        }
        return total;
    }

    public double getAvailableCapacity() {
        return vehicle.getCapacity() - getLoadedWeight();
    }

    public void addShipment(Shipment shipment) {
        requirePreparing();
        if (shipment == null) {
            throw new IllegalArgumentException("El envío es obligatorio.");
        }
        for (Shipment loaded : shipments) {
            if (loaded.getCode().equals(shipment.getCode())) {
                throw new IllegalArgumentException("El envío ya está en este despacho.");
            }
        }
        if (!shipment.isCompatibleWith(vehicle)) {
            throw new IllegalArgumentException("El vehículo no es compatible con el envío.");
        }
        if (shipment.getWeight() > getAvailableCapacity()) {
            throw new IllegalArgumentException("El envío supera la capacidad disponible.");
        }
        shipment.assign();
        shipments.add(shipment);
    }

    public void removeShipment(Shipment shipment) {
        requirePreparing();
        if (!shipments.contains(shipment)) {
            throw new IllegalArgumentException("El envío no pertenece al despacho.");
        }
        shipment.unassign();
        shipments.remove(shipment);
    }

    public int depart() {
        requirePreparing();
        if (shipments.isEmpty()) {
            throw new IllegalStateException("No puede salir un despacho vacío.");
        }
        for (Shipment shipment : shipments) {
            if (shipment.getStatus() != ShipmentStatus.ASSIGNED) {
                throw new IllegalStateException("Todos los envíos deben estar asignados.");
            }
        }
        int retries = 0;
        for (Shipment shipment : shipments) {
            if (shipment.dispatch()) {
                retries++;
            }
            pending.add(shipment.getCode());
        }
        status = DispatchStatus.IN_ROUTE;
        return retries;
    }

    public void recordResult(String code, boolean success) {
        if (status != DispatchStatus.IN_ROUTE) {
            throw new IllegalStateException("El despacho debe estar en ruta.");
        }
        if (!pending.contains(code)) {
            throw new IllegalArgumentException("El envío no tiene un resultado pendiente aquí.");
        }
        for (Shipment shipment : shipments) {
            if (shipment.getCode().equals(code)) {
                shipment.registerAttempt(success);
                pending.remove(code);
                if (pending.isEmpty()) {
                    status = DispatchStatus.CLOSED;
                }
                return;
            }
        }
    }

    private void requirePreparing() {
        if (status != DispatchStatus.PREPARING) {
            throw new IllegalStateException("El despacho debe estar en preparación.");
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "Despacho %d | Vehículo: %s | %s | Envíos: %d | Peso: %.2f kg | Pendientes: %s",
                id, vehicle.getPlate(), status, shipments.size(), getLoadedWeight(), pending);
    }
}
