package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        doSomething(() ->  {
            final var x = 3;
            /* return? */x;
            }
            );
    }
}