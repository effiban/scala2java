package dummy;

import org.jooq.lambda.tuple.Tuple.tuple;

public class Sample {

    public void foo() {
        doSomething(tuple(1,
                2,
                3));
    }
}