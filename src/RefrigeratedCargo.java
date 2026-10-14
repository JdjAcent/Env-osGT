public class RefrigeratedCargo extends Shipment {
    private double temperature;

    public RefrigeratedCargo(String code, String senderName, String recipientName,
                             double weight, DestinationType destinationType, double temperature) {
        super(code, senderName, recipientName, weight, destinationType);
        if (destinationType != DestinationType.URBAN) {
            throw new IllegalArgumentException("La carga refrigerada solo admite destino urbano.");
        }
        if (!Double.isFinite(temperature) || temperature < -25 || temperature > 8) {
            throw new IllegalArgumentException("La temperatura debe estar entre -25 y 8 °C.");
        }
        this.temperature = temperature;
    }

    public double getTemperature() { return temperature; }

    @Override
    public double calculateCost() { return 25 + 12 * getWeight() + 60; }

    @Override
    public int getEstimatedDays() { return 1; }

    @Override
    public int getMaxAttempts() { return 1; }

    @Override
    public boolean requiresRefrigeration() { return true; }
}
