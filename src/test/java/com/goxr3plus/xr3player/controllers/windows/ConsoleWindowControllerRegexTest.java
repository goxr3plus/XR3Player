package com.goxr3plus.xr3player.controllers.windows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * These tests are written with two purposes:
 * 1. To demonstrate some testing techniques
 * 2. To reveal a probable bug. In a regex,  [+|-] matches any of the characters +, - or |.
 *    Vertical bar is not a logical or in the regex context.
 */
class ConsoleWindowControllerRegexTest {

    private ConsoleWindowController instance;

    @BeforeEach
    void setUp() {
        instance = new ConsoleWindowController();
    }

    @Test
    void createInstance() {
        new ConsoleWindowController();
    }


    @ParameterizedTest
    @CsvSource({
            "'player:22:aWord'",  // Optional sign before number is absent.
            "'player:+22:aWord'", // Optional sign is +
            "'player:-22:aWord'"  // Optional sign is -
    })

    void checkPassingRegexps(String testedString) {
        assertTrue(instance.pattern1.matcher(testedString).matches());
    }

    @ParameterizedTest
    @CsvSource({
            "'player:|22:aWord'",  // Vertical bar before the number is not permitted
            "'player:*22:aWord'",  // * before the number is not permitted
            "'Player:|22:aWord'"  // Capital P not permitted
    })

    void checkFailingRegexps(String testedString) {
        assertFalse(instance.pattern1.matcher(testedString).matches(), "The tested string should not match.");
    }

    private static Stream<Arguments> stringsForPattern1() {
        return Stream.of(
                Arguments.of("player:22:anyWwords", true),
                Arguments.of("player:+22:anyWwords", true),
                Arguments.of("player:+22:two words", false), // embedded spaces not permitted
                Arguments.of("player:|22:aWord", false),  // Vertical bar not permitted
                Arguments.of("musicplayer:22:aWord", false) // The first wort must be "player"
        );
    }

    @ParameterizedTest
    @MethodSource("stringsForPattern1")
    void testPattern1(String testedString, boolean expectedToMatch) {
        assertEquals(expectedToMatch, instance.pattern1.matcher(testedString).matches());
    }

    private static Stream<Arguments> stringsForPattern2() {
        return Stream.of(
                Arguments.of("player:22:aWord:3", true),
                Arguments.of("player:22:aWord:", false), // Last number is missing
                Arguments.of("player:22:aWord:3 ", false) // Extra space after
        );
    }

    @ParameterizedTest
    @MethodSource("stringsForPattern2")
    void testPattern2(String testedString, boolean expectedToMatch) {
        assertEquals(expectedToMatch, instance.pattern2.matcher(testedString).matches());
    }

    private static Stream<Arguments> stringsForPattern3() {
        return Stream.of(
                Arguments.of("player:33:aWord:3:s", true), // permit s, m or h after tha last colon
                Arguments.of("player:33:aWord:3:m", true), // permit s, m or h after tha last colon
                Arguments.of("player:33:aWord:3:h", true), // permit s, m or h after tha last colon
                Arguments.of("player:33:aWord:3:|", false), // don't permit | after last colon
                Arguments.of("player:33:aWord:3:s ", false)  // Don't permit trailing space

        );
    }

    @ParameterizedTest
    @MethodSource("stringsForPattern3")
    void testPattern3(String testedString, boolean expectedToMatch) {
        assertEquals(expectedToMatch, instance.pattern3.matcher(testedString).matches());
    }


}