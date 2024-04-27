package dummy;

import java.util.Map.entry;
import org.jooq.lambda.tuple.Tuple.tuple;

public class Sample {
    public final int x = entry(tuple(1,
            2,
            3), tuple(4,
            5,
            6))
            .v1.v1;
}