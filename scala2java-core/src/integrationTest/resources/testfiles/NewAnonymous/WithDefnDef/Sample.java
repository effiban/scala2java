package dummy;


public class Sample {
    public final Foo foo = new Foo() {

        int bar(final int x) {
            return x + 1;
        }
    }
    ;
}