package flc.model;

public enum ExerciseType {
    YOGA("Yoga", 12.0),
    ZUMBA("Zumba", 10.0),
    AQUACISE("Aquacise", 8.0),
    BOX_FIT("Box Fit", 15.0),
    BODY_BLITZ("Body Blitz", 11.0);

    private final String name;
    private final double price;

    ExerciseType(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name;
    }
}
