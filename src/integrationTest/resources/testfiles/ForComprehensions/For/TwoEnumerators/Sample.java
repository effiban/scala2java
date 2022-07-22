package dummy;


public class Sample {

    public Sample() {
    }

    public void foo() {
        xs.forEach(x -> ys.forEach(y ->  {
                doSomething(x, y);
            }
            ));
    }
}