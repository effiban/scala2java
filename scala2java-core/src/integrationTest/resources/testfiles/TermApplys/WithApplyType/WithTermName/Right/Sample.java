package dummy;

import io.vavr.control.Either;

public class Sample {

    public void foo() {
        Either.<Err, int>right(1);
    }
}