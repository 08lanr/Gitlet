package gitlet;
import java.math.*;
import java.security.spec.NamedParameterSpec;

public class min {
    public static void main(String[] args) {
        fizzBuzzBoom(10);
    }
    public static int fizzBuzzBoom(int n) {
        int [] numSteps;
        numSteps = new int[n+1];
        numSteps[1] = 0;
        for (int i = 2; i<=n; i += 1) {
            numSteps[i] = 1 + numSteps[i-1];
            if (i % 2 == 0) numSteps[i] = Math.min(numSteps[i], numSteps[i/2]+1);
            if (i % 3 == 0) numSteps[i] = Math.min(numSteps[i], numSteps[i/3]+1);
        }
        return numSteps[n];
    }
}
