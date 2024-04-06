package dummy;

import java.lang.System.out;

public class Sample {

    public void foo() {
        switch (str) {
            case "one" -> out.println(1);
            default -> out.println("other");
        }
    }
}