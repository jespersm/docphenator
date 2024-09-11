package io.github.jespersm.docphenator;

import io.github.nianna.api.Hyphenator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HyphenationManager {
    private Map<String, Optional<Hyphenator>> languageTagToHyphenator = new HashMap<>();

    /**
     * Obtain a Hyphenator for a given language tag.
     * @param tag BCP 47 language tag, like "da", or "en-UK".
     * @return A functioning Hyphenator for the given language tag.
     * @throws LanguageNotSupportedException If no hyphenation patterns are available for the given language tag.
     */
    synchronized Hyphenator getHyphenator(String tag) throws LanguageNotSupportedException {
        var maybeKnown = languageTagToHyphenator.get(tag);
        if (maybeKnown != null) {
            return maybeKnown.orElseThrow(() -> new LanguageNotSupportedException(tag));
        }
        // Try to load the hyphenation patterns for the given language tag
        try {
            var patterns = loadHyphenatorPatterns(tag);
            var hyphenator = new Hyphenator(patterns);
            languageTagToHyphenator.put(tag, Optional.of(hyphenator));
            return hyphenator;
        } catch (FileNotFoundException e) {
            // If not found, remember that this language tag is not supported
            languageTagToHyphenator.put(tag, Optional.empty());
            throw new LanguageNotSupportedException(tag);
        }
    }

    private List<String> loadHyphenatorPatterns(String tag) throws FileNotFoundException {
        try (InputStream patterns = findPatternsForLanguage(tag);
            InputStreamReader inputStreamReader = new InputStreamReader(patterns, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            return bufferedReader.lines().toList();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("No hyphenation patterns for language tag " + tag + " could be loaded", e);
        }
    }

    private InputStream findPatternsForLanguage(String tag) throws FileNotFoundException {
        var stream = this.getClass().getResourceAsStream("/patterns/hyph-" + tag + ".pat.txt");
        if (stream == null) {
            throw new FileNotFoundException("No hyphenation patterns found for language tag " + tag);
        }
        return stream;
    }
}
