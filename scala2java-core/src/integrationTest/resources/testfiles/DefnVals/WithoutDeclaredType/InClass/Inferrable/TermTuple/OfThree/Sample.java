package dummy;

import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple.tuple;

public class Sample {
    public final Tuple3<String, int, double> x = tuple("a",
            1,
            2.0);
}