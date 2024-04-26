package dummy;

import io.vavr.control.Try.failure;

public class Sample {

    public void foo() {
        failure(new RuntimeException());
    }
}