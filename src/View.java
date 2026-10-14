import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class View {
    private Scanner input;

    public View() {
        input = new Scanner(System.in);
    }

    public void showMenu() {
        System.out.println("\nENVÍAGT");
        System.out.println("1. Cotizar envío");
        System.out.println("2. Registrar envío");
        System.out.println("3. Registrar vehículo");
        System.out.println("4. Crear despacho");
        System.out.println("5. Asignar envío");
        System.out.println("6. Retirar envío");
        System.out.println("7. Dar salida a despacho");
        System.out.println("8. Registrar resultado de entrega");
        System.out.println("9. Consultar envíos");
        System.out.println("10. Consultar vehículos");
        System.out.println("11. Consultar despachos");
        System.out.println("12. Reporte");
        System.out.println("0. Salir");
    }

    public int readOption() { return readInt("Opción: "); }

    public String readText(String message) {
        while (true) {
            System.out.print(message);
            if (!input.hasNextLine()) {
                throw new NoSuchElementException("Entrada finalizada.");
            }
            String value = input.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            showError("El valor no puede estar vacío.");
        }
    }

    public int readInt(String message) {
        while (true) {
            try {
                return Integer.parseInt(readText(message));
            } catch (NumberFormatException exception) {
                showError("Ingresa un número entero válido.");
            }
        }
    }

    public double readDouble(String message) {
        while (true) {
            try {
                double value = Double.parseDouble(readText(message));
                if (Double.isFinite(value)) {
                    return value;
                }
            } catch (NumberFormatException exception) {
                showError("Usa un número válido y punto para los decimales.");
                continue;
            }
            showError("El número debe ser finito.");
        }
    }

    public boolean readBoolean(String message) {
        while (true) {
            String value = readText(message + " (s/n): ").toLowerCase(Locale.ROOT);
            if (value.equals("s") || value.equals("si") || value.equals("sí")) {
                return true;
            }
            if (value.equals("n") || value.equals("no")) {
                return false;
            }
            showError("Responde s o n.");
        }
    }

    public void showMessage(String message) { System.out.println(message); }
    public void showError(String message) { System.out.println("Error: " + message); }
    public void showReport(String report) { System.out.println(report); }
}
