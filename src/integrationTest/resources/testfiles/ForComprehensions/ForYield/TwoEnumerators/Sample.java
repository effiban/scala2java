package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        xs.flatMap(x -> ys.map(y -> doSomething(x, y)));
    }
}