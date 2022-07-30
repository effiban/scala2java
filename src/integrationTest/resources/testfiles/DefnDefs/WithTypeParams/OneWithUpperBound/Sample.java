package dummy;


public interface Sample {

    default <T extends U> void foo() {
    }
}