package com.goxr3plus.xr3player.controllers.windows;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleWindowControllerTest {

    @Test
    void createInstance() {
        new ConsoleWindowController();
    }


    @Test
    void checkRegexps() {
        // TODO: change into a parameterized test. (requires support in pom.xml)
        final ConsoleWindowController instance = new ConsoleWindowController();

        assertTrue(instance.pattern1.matcher("player:|22:aWord").matches());
        // TODO: Was this really expected to pass? Or are only + and - allowed before the number?

    }


}