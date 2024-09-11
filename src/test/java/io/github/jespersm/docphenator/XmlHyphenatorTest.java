package io.github.jespersm.docphenator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class XmlHyphenatorTest {

    @org.junit.jupiter.api.Test
    void hyphenateDocument() throws LanguageNotSupportedException, ParserConfigurationException, TransformerException, IOException, SAXException {

        String xmlDocument = """
                <document>
                  <p>
                    I 1980'erne var en computer eller et tv-spil er en <span lang='en-GB'>fantastic</span> ting at have.
                  </p>
                  <p>
                    I Polen havde de ikke computere i de små hjem i 1980'erne:
                    <span lang='pl'>
                      Stany Zjednoczone nałożyły sankcje gospodarcze na Polską Rzeczpospolitą Ludową:
                      Komputery  były dostępne tylko w dużych firmach.
                    </span>
                  </p>
                  <p lang='xy'>
                    In the 1980s, a computer or a video game was a fantastic thing to have.
                  </p>
                </document>
                """;
        Document doc = DocumentBuilderFactory.newDefaultNSInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xmlDocument.getBytes(StandardCharsets.UTF_8)), "UTF-8");

        new XmlHyphenator().hyphenateDocument(doc, Optional.of("da"));

        var sink = new ByteArrayOutputStream();
        javax.xml.transform.TransformerFactory.newDefaultInstance().newTransformer().transform
                (new DOMSource(doc), new StreamResult(sink));
        var hyphenated = sink.toString(StandardCharsets.UTF_8);

        assertNotEquals(xmlDocument, hyphenated);
        System.out.println(hyphenated);
        assertTrue(hyphenated.contains("com\u00ADpu\u00ADte\u00ADre"));
    }
}