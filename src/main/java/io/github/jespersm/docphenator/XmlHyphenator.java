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

public class XmlHyphenator {
    private final HyphenationManager manager = new HyphenationManager();

    public static final String SOFT_HYPHEN = "\u00AD";
    private final static Hyphenator NULL_HYPHENATOR = new Hyphenator(List.of(), new HyphenatorProperties(1000, 1000));

    public void hyphenateDocument(Document doc, Optional<String> defaultLanguage) throws LanguageNotSupportedException {
        Deque<Hyphenator> hyphenators = new ArrayDeque<>();
        if (defaultLanguage.isPresent()) {
            hyphenators.push(manager.getHyphenator(defaultLanguage.get()));
        } else {
            hyphenators.push(NULL_HYPHENATOR);
        }
        hyphenateElement(doc.getDocumentElement(), hyphenators);
    }

    private void hyphenateElement(Element documentElement, Deque<Hyphenator> hyphenators) {
        var lang = documentElement.getAttribute("lang");
        if (!lang.isEmpty()) {
            try {
                hyphenators.push(manager.getHyphenator(lang));
            } catch (LanguageNotSupportedException e) {
                hyphenators.push(NULL_HYPHENATOR);
            }
        } // else use the current hyphenator

        var childNodes = documentElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            var node = childNodes.item(i);
            if (node instanceof Element elem) {
                hyphenateElement(elem, hyphenators);
            } else if (node instanceof Text text) {
                hyphenateText(text, hyphenators.peek());
            }
        }
        if (!lang.isEmpty()) {
            hyphenators.pop();
        }
    }

    private static void hyphenateText(Text text, Hyphenator hyphenator) {
        if (hyphenator != null) {
            var plainText = text.getWholeText();
            text.replaceWholeText(hyphenator.hyphenateText(plainText).read(" ", SOFT_HYPHEN));
        }
    }

}