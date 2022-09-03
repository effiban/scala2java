package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        IntStream.rangeClosed(1, 4).forEach(i -> doSomething(i));
    }
}