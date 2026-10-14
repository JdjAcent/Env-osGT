public class Envelope extends Shipment {
    public Envelope(String code, String senderName, String recipientName,
                    double weight, DestinationType destinationType) {
        super(code, senderName, recipientName, weight, destinationType);
        if (weight > 0.5) {
            throw new IllegalArgumentException("Un sobre no puede superar 0.5 kg.");
        }
    }

    @Override
    public double calculateCost() {
        return getDestinationType() == DestinationType.URBAN ? 20 : 35;
    }

    @Override
    public int getEstimatedDays() {
        return getDestinationType() == DestinationType.URBAN ? 1 : 2;
    }

    @Override
    public int getMaxAttempts() { return 3; }
}
