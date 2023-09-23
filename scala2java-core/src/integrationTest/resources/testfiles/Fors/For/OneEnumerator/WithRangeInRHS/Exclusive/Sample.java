package dummy;


public class Sample {

    public void foo() {
        IntStream.range(0, 4)
                .forEach(i -> doSomething(i));
    }
}