package dummy;


public interface Sample {

    @MyAnnot1
    @MyAnnot2
    default void foo() {
    }
}