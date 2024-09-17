package io.github.jespersm.docphenator;

import io.github.nianna.api.Hyphenator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Manages the loading and caching of hyphenation patterns for different languages.
 */
public class HyphenationManager {

    /**
     * Create a blank HyphenationManager.
     */
    public HyphenationManager() {
    }

    private final Map<String, Optional<Hyphenator>> languageTagToHyphenator = new HashMap<>();

    /**
     * Obtain a Hyphenator for a given language tag. If the Hyphenator for the given language tag has already been
     * loaded, it will be returned from cache. Otherwise, the hyphenation patterns for the given language tag will be
     * loaded and a new Hyphenator will be created in a thread safe manner.
     *
     * @param tag BCP 47 language tag, like "da", or "en-UK".
     * @return A functioning Hyphenator for the given language tag.
     * @throws LanguageNotSupportedException If no hyphenation patterns are available for the given language tag.
     */
    public synchronized Hyphenator getHyphenator(String tag) throws LanguageNotSupportedException {
        var normalizedTag = tag.toLowerCase(Locale.ENGLISH).replace("_", "-");

        var maybeKnown = languageTagToHyphenator.get(normalizedTag);
        if (maybeKnown != null) {
            return maybeKnown.orElseThrow(() -> new LanguageNotSupportedException(tag));
        }
        // Try to load the hyphenation patterns for the given language tag
        try {
            var patterns = loadHyphenatorPatterns(normalizedTag);
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
