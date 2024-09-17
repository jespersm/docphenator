package io.github.jespersm.docphenator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HyphenationManagerTest {

    @Test
    void test_getHyphenator_ok() throws LanguageNotSupportedException {
        var target = new HyphenationManager();
        Assertions.assertNotNull(target.getHyphenator("da"));
    }

    @Test
    void test_getHyphenator_notFound() {
        var target = new HyphenationManager();
        Assertions.assertThrows(LanguageNotSupportedException.class, () -> target.getHyphenator("quux"));
    }

    @Test
    void test_getHyphenator_same() throws LanguageNotSupportedException {
        var target = new HyphenationManager();
        var daHyphenator1 = target.getHyphenator("da");
        var daHyphenator2 = target.getHyphenator("DA");

        assertSame(daHyphenator1, daHyphenator2);
    }

}