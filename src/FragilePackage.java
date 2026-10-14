public class FragilePackage extends Package {
    private double declaredValue;

    public FragilePackage(String code, String senderName, String recipientName,
                         double weight, DestinationType destinationType,
                         double length, double width, double height, double declaredValue) {
        super(code, senderName, recipientName, weight, destinationType, length, width, height);
        if (!Double.isFinite(declaredValue) || declaredValue <= 0) {
            throw new IllegalArgumentException("El valor declarado debe ser positivo y finito.");
        }
        this.declaredValue = declaredValue;
    }

    public double getDeclaredValue() { return declaredValue; }

    @Override
    public double calculateCost() {
        return super.calculateCost() + 15 + 0.03 * declaredValue;
    }

    @Override
    public int getEstimatedDays() { return super.getEstimatedDays() + 1; }

    @Override
    public int getMaxAttempts() { return 2; }
}
