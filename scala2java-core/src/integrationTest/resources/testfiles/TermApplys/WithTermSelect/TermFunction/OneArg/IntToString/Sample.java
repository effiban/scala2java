package dummy;


public class Sample {

    public void foo() {
        ((Function<int, String>)(int x) -> "bla").apply(3);
    }
}