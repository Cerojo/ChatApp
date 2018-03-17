import java.util.Arrays;

/**
 * Created by Cecil Hlungwana on 3/17/2018.
 */
public class Main {
    public static void main(String[] args) {
        String test = "A.png";
        String array = test.substring(test.indexOf(".")+1,test.length());
        System.out.println(array);
    }
}
