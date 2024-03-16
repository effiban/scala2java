package dummy;

import java.util.stream.IntStream.rangeClosed;

public class Sample {

    public void foo() {
        rangeClosed(1, 4)
        .forEach(i -> doSomething(i));
    }
}