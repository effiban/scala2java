package dummy;


public class Sample {
    private final String param1;
    private final int param2;

    public Sample(final String param1, final int param2) {
        this.param1 = param1;
        this.param2 = param2;
        foo(param1);
        bar(param2);
    }
}