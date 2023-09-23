package dummy;


public class Sample {

    public void foo() {
        ((Supplier<int>)() -> 3).get();
    }
}