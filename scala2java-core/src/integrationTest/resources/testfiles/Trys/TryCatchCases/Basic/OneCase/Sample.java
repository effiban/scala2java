package dummy;


public class Sample {

    public void foo() {
        try {
            doSomething();
        }
        catch (Throwable e) {
            handleError(e);
        }
    }
}