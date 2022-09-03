package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        try {
            doSomething;
        }
        catch (final IllegalStateException e) {
            handleIllegalState(e);
        }
        catch (final Throwable e) {
            handleError(e);
        }
    }
}