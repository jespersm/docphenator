package io.github.jespersm.docphenator;

import io.github.nianna.api.Hyphenator;
import io.github.nianna.api.HyphenatorProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * Hyphenates text in an XML document.
 */
public class XmlHyphenator {

    /**
     * Creates a new XmlHyphenator.
     */
    public XmlHyphenator() {
    }

    private final HyphenationManager manager = new HyphenationManager();

    private static final String SOFT_HYPHEN = "\u00AD";
    private final static Hyphenator NULL_HYPHENATOR = new Hyphenator(List.of(), new HyphenatorProperties(1000, 1000));

    /**
     * Hyphenates the text in the given document.
     * @param doc DOM Document to traverse.
     * @param defaultLanguage default language to use if no language is specified throughout the document.
     * @throws LanguageNotSupportedException if the default language is not supported.
     */
    public void hyphenateDocument(Document doc, Optional<String> defaultLanguage) throws LanguageNotSupportedException {
        hyphenateElement(doc.getDocumentElement(), defaultLanguage);
    }

    /**
     * Hyphenates the text in the given document.
     * @param element DOM Element to traverse.
     * @param defaultLanguage default language to use if no language is specified in the element and it's children.
     * @throws LanguageNotSupportedException if the default language is not supported.
     */
    public void hyphenateElement(Element element, Optional<String> defaultLanguage) throws LanguageNotSupportedException {
        Deque<Hyphenator> hyphenators = new ArrayDeque<>();
        if (defaultLanguage.isPresent()) {
            hyphenators.push(manager.getHyphenator(defaultLanguage.get()));
        } else {
            hyphenators.push(NULL_HYPHENATOR);
        }
        if (element.getTagName().equals("style") |)
        hyphenateElement(element, hyphenators);
    }

    private void hyphenateElement(Element documentElement, Deque<Hyphenator> hyphenators) {
        var lang = documentElement.getAttribute("lang");
        if (!lang.isEmpty()) {
            // Language is overridden from this element down
            try {
                hyphenators.push(manager.getHyphenator(lang));
            } catch (LanguageNotSupportedException e) {
                hyphenators.push(NULL_HYPHENATOR);
            }
            processElementContents(documentElement, hyphenators);
            hyphenators.pop();
        } else {
            // else use the current hyphenator
            processElementContents(documentElement, hyphenators);
        }
    }

    private void processElementContents(Element documentElement, Deque<Hyphenator> hyphenators) {
        var childNodes = documentElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            var node = childNodes.item(i);
            if (node instanceof Element elem) {
                hyphenateElement(elem, hyphenators);
            } else if (node instanceof Text text) {
                hyphenateText(text, hyphenators.peek());
            }
        }
    }

    private static void hyphenateText(Text text, Hyphenator hyphenator) {
        if (hyphenator != null) {
            var plainText = text.getWholeText();
            var replaced = hyphenator.hyphenateText(plainText).read(" ", SOFT_HYPHEN);
            if (plainText.substring(Integer.max(0, plainText.length() - 1)).isBlank()) {
                replaced = replaced + " ";
            }
            text.replaceWholeText(replaced);
        }
    }

}