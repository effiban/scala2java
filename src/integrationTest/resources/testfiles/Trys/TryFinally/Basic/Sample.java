package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        try {
            doSomething();
        }
        finally {
            cleanUp();
        }
    }
}