package dummy;

import java.util.List;

public class Sample {

    public interface MyType<T extends U> extends List<T> {
    }
}