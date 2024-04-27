package dummy;

import java.util.function.Supplier;

public class Sample {

    public void foo() {
        ((Supplier<int>)() -> 3).get();
    }
}