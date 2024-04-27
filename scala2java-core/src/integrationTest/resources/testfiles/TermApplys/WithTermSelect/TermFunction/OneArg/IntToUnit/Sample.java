package dummy;

import java.lang.System.out;
import java.util.function.Consumer;

public class Sample {

    public void foo() {
        ((Consumer<int>)(int x) -> out.print(x)).accept(3);
    }
}