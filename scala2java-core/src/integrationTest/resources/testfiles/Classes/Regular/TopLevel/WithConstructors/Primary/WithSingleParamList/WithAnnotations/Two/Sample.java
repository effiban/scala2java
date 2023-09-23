package dummy;


public class Sample {
    private final String param1;
    private final int param2;

    public Sample(@MyAnnot1 @MyAnnot2 final String param1, final int param2) {
        this.param1 = param1;
        this.param2 = param2;
    }
}