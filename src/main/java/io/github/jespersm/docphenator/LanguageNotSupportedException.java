package io.github.jespersm.docphenator;

/**
 * Exception thrown when no hyphenation patterns are available for a given language tag.
 */
public class LanguageNotSupportedException extends Exception {

    /**
     * Constructs a new exception with the given language tag.
     * @param languageTag the language tag for which no hyphenation patterns are available
     */
    public LanguageNotSupportedException(String languageTag) {
        super("No hyphenation patterns available for language tag " + languageTag);
    }
}
