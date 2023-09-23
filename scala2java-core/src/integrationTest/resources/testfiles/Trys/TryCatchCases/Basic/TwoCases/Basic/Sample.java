package dummy;


public class Sample {

    public void foo() {
        try {
            doSomething();
        }
        catch (IllegalStateException e) {
            handleIllegalState(e);
        }
        catch (Throwable e) {
            handleError(e);
        }
    }
}