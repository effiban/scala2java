package dummy;

import io.vavr.control.Try;

public class Sample {

    public void foo() {
        Try.<int>failure(new RuntimeException());
    }
}