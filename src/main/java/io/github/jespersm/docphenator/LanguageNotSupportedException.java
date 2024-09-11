package io.github.jespersm.docphenator;

public class LanguageNotSupportedException extends Exception {
    public LanguageNotSupportedException(String languageTag) {
        super("No hyphenation patterns available for language tag " + languageTag);
    }
}
