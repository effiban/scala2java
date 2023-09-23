package dummy;


public class Sample {
    private final Supplier<String> param1;
    private final Supplier<int> param2;

    public Sample(final Supplier<String> param1, final Supplier<int> param2) {
        this.param1 = param1;
        this.param2 = param2;
    }
}