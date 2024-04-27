package dummy;

import java.util.function.Function;

public class Sample {

    public void foo() {
        ((Function<int, String>)(int x) -> "bla").apply(3);
    }
}