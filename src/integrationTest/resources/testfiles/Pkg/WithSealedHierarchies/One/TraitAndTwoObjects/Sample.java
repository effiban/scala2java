package dummy;


public sealed interface Sample permits Sample1, Sample2 {
}

public final class Sample1 implements Sample {
}

public final class Sample2 implements Sample {
}