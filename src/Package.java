public class Package extends Shipment {
    private double length;
    private double width;
    private double height;

    public Package(String code, String senderName, String recipientName,
                   double weight, DestinationType destinationType,
                   double length, double width, double height) {
        super(code, senderName, recipientName, weight, destinationType);
        if (!Double.isFinite(length) || length <= 0
                || !Double.isFinite(width) || width <= 0
                || !Double.isFinite(height) || height <= 0
                || !Double.isFinite(length * width * height)) {
            throw new IllegalArgumentException("Las dimensiones deben ser positivas y válidas.");
        }
        this.length = length;
        this.width = width;
        this.height = height;
    }

    public double getLength() { return length; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public double getBillableWeight() {
        return Math.ceil(Math.max(getWeight(), length * width * height / 5000.0));
    }

    @Override
    public double calculateCost() {
        double base = getDestinationType() == DestinationType.URBAN ? 25 : 40;
        return base + 8 * getBillableWeight();
    }

    @Override
    public int getEstimatedDays() {
        return getDestinationType() == DestinationType.URBAN ? 2 : 4;
    }

    @Override
    public int getMaxAttempts() { return 3; }
}
