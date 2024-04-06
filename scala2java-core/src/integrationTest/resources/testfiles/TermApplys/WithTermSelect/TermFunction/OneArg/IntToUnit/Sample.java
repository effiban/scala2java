package dummy;

import java.lang.System.out;

public class Sample {

    public void foo() {
        ((Consumer<int>)(int x) -> out.print(x)).accept(3);
    }
}