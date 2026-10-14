import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ShippingSystem {
    private Map<String, Shipment> shipments;
    private Map<String, Vehicle> vehicles;
    private Map<Integer, Dispatch> dispatches;
    private Map<String, Double> registrationIncome;
    private Map<String, Double> retryIncome;
    private double totalRegistrationIncome;
    private double totalRetryIncome;
    private int nextDispatchId;

    public ShippingSystem() {
        shipments = new LinkedHashMap<>();
        vehicles = new LinkedHashMap<>();
        dispatches = new LinkedHashMap<>();
        registrationIncome = new LinkedHashMap<>();
        retryIncome = new LinkedHashMap<>();
        nextDispatchId = 1;
    }

    public double quote(Shipment shipment) {
        if (shipment == null) {
            throw new IllegalArgumentException("El envío es obligatorio.");
        }
        double cost = shipment.calculateCost();
        if (!Double.isFinite(cost) || cost < 0) {
            throw new IllegalArgumentException("El costo calculado no es válido.");
        }
        return cost;
    }

    public void registerShipment(Shipment shipment) {
        double cost = quote(shipment);
        if (shipments.containsKey(shipment.getCode())) {
            throw new IllegalArgumentException("Ya existe un envío con ese código.");
        }
        if (shipment.getStatus() != ShipmentStatus.IN_WAREHOUSE
                || shipment.getDeliveryAttempts() != 0) {
            throw new IllegalArgumentException("Solo se pueden registrar envíos nuevos.");
        }
        shipments.put(shipment.getCode(), shipment);
        String category = shipment.getClass().getSimpleName();
        registrationIncome.put(category, registrationIncome.getOrDefault(category, 0.0) + cost);
        totalRegistrationIncome += cost;
    }

    public void registerVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("El vehículo es obligatorio.");
        }
        if (vehicles.containsKey(vehicle.getPlate())) {
            throw new IllegalArgumentException("Ya existe un vehículo con esa placa.");
        }
        vehicles.put(vehicle.getPlate(), vehicle);
    }

    public Dispatch createDispatch(String plate) {
        Vehicle vehicle = vehicles.get(plate == null ? "" : plate.trim().toUpperCase(Locale.ROOT));
        if (vehicle == null) {
            throw new IllegalArgumentException("No existe un vehículo con esa placa.");
        }
        for (Dispatch dispatch : dispatches.values()) {
            if (dispatch.getVehicle() == vehicle && dispatch.getStatus() != DispatchStatus.CLOSED) {
                throw new IllegalStateException("El vehículo ya tiene un despacho abierto.");
            }
        }
        Dispatch dispatch = new Dispatch(nextDispatchId, vehicle);
        dispatches.put(nextDispatchId, dispatch);
        nextDispatchId++;
        return dispatch;
    }

    public void assign(int dispatchId, String code) {
        findDispatch(dispatchId).addShipment(findShipment(code));
    }

    public void remove(int dispatchId, String code) {
        findDispatch(dispatchId).removeShipment(findShipment(code));
    }

    public void depart(int dispatchId) {
        Dispatch dispatch = findDispatch(dispatchId);
        int retries = dispatch.depart();
        for (Shipment shipment : dispatch.getShipments()) {
            if (shipment.getDeliveryAttempts() > 0) {
                String category = shipment.getClass().getSimpleName();
                retryIncome.put(category, retryIncome.getOrDefault(category, 0.0) + 15);
            }
        }
        totalRetryIncome += retries * 15;
    }

    public void recordResult(int dispatchId, String code, boolean success) {
        findDispatch(dispatchId).recordResult(code == null ? "" : code.trim(), success);
    }

    public Collection<Shipment> getShipments() {
        return Collections.unmodifiableCollection(shipments.values());
    }

    public Collection<Vehicle> getVehicles() {
        return Collections.unmodifiableCollection(vehicles.values());
    }

    public Collection<Dispatch> getDispatches() {
        return Collections.unmodifiableCollection(dispatches.values());
    }

    public Map<String, Double> getRegistrationIncome() {
        return Collections.unmodifiableMap(registrationIncome);
    }

    public Map<String, Double> getRetryIncome() {
        return Collections.unmodifiableMap(retryIncome);
    }

    public double getTotalRegistrationIncome() { return totalRegistrationIncome; }
    public double getTotalRetryIncome() { return totalRetryIncome; }

    public String report() {
        StringBuilder result = new StringBuilder("INGRESOS POR CATEGORÍA\n");
        Set<String> categories = new LinkedHashSet<>(registrationIncome.keySet());
        categories.addAll(retryIncome.keySet());
        for (String category : categories) {
            double registration = registrationIncome.getOrDefault(category, 0.0);
            double retries = retryIncome.getOrDefault(category, 0.0);
            result.append(String.format(Locale.ROOT,
                    "%s | Registro: Q%.2f | Reintentos: Q%.2f | Total: Q%.2f%n",
                    category, registration, retries, registration + retries));
        }
        result.append(String.format(Locale.ROOT,
                "Total registro: Q%.2f%nTotal reintentos: Q%.2f%nTOTAL: Q%.2f%n",
                totalRegistrationIncome, totalRetryIncome,
                totalRegistrationIncome + totalRetryIncome));
        result.append("\nENVÍOS POR ESTADO\n");
        for (ShipmentStatus status : ShipmentStatus.values()) {
            int count = 0;
            for (Shipment shipment : shipments.values()) {
                if (shipment.getStatus() == status) {
                    count++;
                }
            }
            result.append(status).append(": ").append(count).append('\n');
        }
        return result.toString();
    }

    private Shipment findShipment(String code) {
        Shipment shipment = shipments.get(code == null ? "" : code.trim());
        if (shipment == null) {
            throw new IllegalArgumentException("No existe un envío con ese código.");
        }
        return shipment;
    }

    private Dispatch findDispatch(int id) {
        Dispatch dispatch = dispatches.get(id);
        if (dispatch == null) {
            throw new IllegalArgumentException("No existe ese despacho.");
        }
        return dispatch;
    }
}
