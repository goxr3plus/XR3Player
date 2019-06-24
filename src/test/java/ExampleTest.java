import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExampleTest {
    @Test
    void example() {
        String message ="This test is a placeholder for the directory that should hold tests. Remove it when there are real tests!";
        assertEquals(1, 2, message);  // TODO: remove this failing test.
    }


    // A simple way to define a parameterized test. Not the most powerful, though.
    @ParameterizedTest
    @CsvSource({
            "1, 2, 3",
            "10, 12, 22",
            "1, -100, -99",
            "0, 0, 17"  // Expected to fail. TODO: Remove Failing tests should probably not be saved.
    })
    void parameterizedExample(int firstTerm, int secondTerm, int expectedSum) {
    assertEquals(expectedSum, firstTerm + secondTerm);
    }
}
