package dummy;


public class Sample {

    public void foo() {
        xs.flatMap(x -> ys.flatMap(y -> zs.map(z -> doSomething(x,
            y,
            z))));
    }
}