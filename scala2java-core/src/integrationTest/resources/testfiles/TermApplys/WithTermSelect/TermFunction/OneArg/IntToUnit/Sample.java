package dummy;


public class Sample {

    public void foo() {
        ((Consumer<int>)(int x) -> print(x)).accept(3);
    }
}