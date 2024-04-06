package dummy;

import java.util.List;
import java.util.List.of;

public class Sample {
    public final List<int> x = of(1, 2)
    .subList(0, 1);
}