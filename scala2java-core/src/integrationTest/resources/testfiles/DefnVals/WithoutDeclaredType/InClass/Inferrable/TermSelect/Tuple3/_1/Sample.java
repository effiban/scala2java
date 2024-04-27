package dummy;

import org.jooq.lambda.tuple.Tuple.tuple;

public class Sample {
    public final int x = tuple(1,
            2,
            3)
            .v1;
}