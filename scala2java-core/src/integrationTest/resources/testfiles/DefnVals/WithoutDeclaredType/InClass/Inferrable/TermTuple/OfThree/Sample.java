package dummy;

import org.jooq.lambda.tuple.Tuple3;

public class Sample {
    public final Tuple3<String, int, double> x = Tuple.tuple("a",
            1,
            2.0);
}