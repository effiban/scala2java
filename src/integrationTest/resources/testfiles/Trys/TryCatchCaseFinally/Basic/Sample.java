package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        try {
            doSomething();
        }
        catch (final Throwable e) {
            handleError();
        }
        finally {
            cleanUp();
        }
    }
}