import java.util.Locale;
import java.util.NoSuchElementException;

public class Main {
    public static void main(String[] args) {
        View view = new View();
        ShippingSystem system = new ShippingSystem();
        boolean running = true;

        while (running) {
            try {
                view.showMenu();
                int option = view.readOption();

                switch (option) {
                    case 0:
                        running = false;
                        break;
                    case 1:
                    case 2: {
                        view.showMessage("1. Sobre | 2. Paquete | 3. Frágil | 4. Refrigerado");
                        int type = view.readInt("Categoría: ");
                        if (type < 1 || type > 4) {
                            throw new IllegalArgumentException("Categoría inválida.");
                        }
                        String code = view.readText("Código: ");
                        String sender = view.readText("Remitente: ");
                        String recipient = view.readText("Destinatario: ");
                        double weight = view.readDouble("Peso en kg: ");
                        int destinationOption = view.readInt("Destino (1 urbano, 2 departamental): ");
                        if (destinationOption != 1 && destinationOption != 2) {
                            throw new IllegalArgumentException("Destino inválido.");
                        }
                        DestinationType destination = destinationOption == 1
                                ? DestinationType.URBAN : DestinationType.DEPARTMENTAL;
                        Shipment shipment;

                        if (type == 1) {
                            shipment = new Envelope(code, sender, recipient, weight, destination);
                        } else if (type == 4) {
                            double temperature = view.readDouble("Temperatura en °C: ");
                            shipment = new RefrigeratedCargo(code, sender, recipient,
                                    weight, destination, temperature);
                        } else {
                            double length = view.readDouble("Largo en cm: ");
                            double width = view.readDouble("Ancho en cm: ");
                            double height = view.readDouble("Alto en cm: ");
                            if (type == 2) {
                                shipment = new Package(code, sender, recipient, weight,
                                        destination, length, width, height);
                            } else {
                                double value = view.readDouble("Valor declarado en Q: ");
                                shipment = new FragilePackage(code, sender, recipient, weight,
                                        destination, length, width, height, value);
                            }
                        }
                        double cost = system.quote(shipment);
                        if (option == 2) {
                            system.registerShipment(shipment);
                            view.showMessage("Envío registrado.");
                        } else {
                            view.showMessage("Cotización sin registro ni cobro.");
                        }
                        view.showMessage(String.format(Locale.ROOT,
                                "Costo: Q%.2f | Entrega estimada: %d días hábiles",
                                cost, shipment.getEstimatedDays()));
                        break;
                    }
                    case 3: {
                        String plate = view.readText("Placa: ");
                        double capacity = view.readDouble("Capacidad en kg: ");
                        boolean refrigeration = view.readBoolean("¿Tiene refrigeración?");
                        system.registerVehicle(new Vehicle(plate, capacity, refrigeration));
                        view.showMessage("Vehículo registrado.");
                        break;
                    }
                    case 4: {
                        Dispatch dispatch = system.createDispatch(view.readText("Placa del vehículo: "));
                        view.showMessage("Despacho creado con ID: " + dispatch.getId());
                        break;
                    }
                    case 5: {
                        int id = view.readInt("ID del despacho: ");
                        String code = view.readText("Código del envío: ");
                        system.assign(id, code);
                        view.showMessage("Envío asignado.");
                        break;
                    }
                    case 6: {
                        int id = view.readInt("ID del despacho: ");
                        String code = view.readText("Código del envío: ");
                        system.remove(id, code);
                        view.showMessage("Envío retirado y disponible en bodega.");
                        break;
                    }
                    case 7: {
                        int id = view.readInt("ID del despacho: ");
                        system.depart(id);
                        view.showMessage("Despacho en ruta. Recargos de reintento registrados si aplican.");
                        break;
                    }
                    case 8: {
                        int id = view.readInt("ID del despacho: ");
                        String code = view.readText("Código del envío: ");
                        boolean success = view.readBoolean("¿Fue entregado?");
                        system.recordResult(id, code, success);
                        view.showMessage("Resultado registrado.");
                        break;
                    }
                    case 9:
                        if (system.getShipments().isEmpty()) {
                            view.showMessage("No hay envíos registrados.");
                        }
                        for (Shipment shipment : system.getShipments()) {
                            view.showMessage(shipment.toString());
                        }
                        break;
                    case 10:
                        if (system.getVehicles().isEmpty()) {
                            view.showMessage("No hay vehículos registrados.");
                        }
                        for (Vehicle vehicle : system.getVehicles()) {
                            view.showMessage(vehicle.toString());
                        }
                        break;
                    case 11:
                        if (system.getDispatches().isEmpty()) {
                            view.showMessage("No hay despachos registrados.");
                        }
                        for (Dispatch dispatch : system.getDispatches()) {
                            view.showMessage(dispatch.toString());
                        }
                        break;
                    case 12:
                        view.showReport(system.report());
                        break;
                    default:
                        view.showError("Opción inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                view.showError(exception.getMessage());
            } catch (NoSuchElementException exception) {
                running = false;
            }
        }
        view.showMessage("Programa finalizado.");
    }
}
