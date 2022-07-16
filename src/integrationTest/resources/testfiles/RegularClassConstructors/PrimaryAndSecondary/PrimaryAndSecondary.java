package dummy;


public class PrimaryAndSecondary {
    private final String param1;
    private final int param2;

    public PrimaryAndSecondary(final String param1, final int param2) {
        this.param1 = param1;
        this.param2 = param2;
    }

    public /* UnknownType */ PrimaryAndSecondary(final String param1,
        final int param2,
        final String param3,
        final int param4) {
        this(param1, param2);
    }
}