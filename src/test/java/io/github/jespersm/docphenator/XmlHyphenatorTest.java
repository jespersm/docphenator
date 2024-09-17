package io.github.jespersm.docphenator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
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
    void test_hyphenateDocument_simple() {
        assertDoesNotThrow(() -> {
            String xmlDocument = """
                    <p lang='da'>Computer</p>
                    """;
            var hyphenated = hyphenateXml(xmlDocument, Optional.empty());
            assertEquals("""
                    <p lang="da">Com\u00ADpu\u00ADter</p>""", hyphenated);
        });
    }

    @org.junit.jupiter.api.Test
    void test_hyphenateDocument_unsupported() {
        assertThrows(LanguageNotSupportedException.class,
                () -> hyphenateXml("<a/>", Optional.of("li-LI"))); // Lilliputian
    }

    @org.junit.jupiter.api.Test
    void test_hyphenateDocument_spaces() {
        assertDoesNotThrow(() -> {
            String xmlDocument = """
                    <p lang='da'>Det <b> nye </b> COMputer</p>
                    """;
            var hyphenated = hyphenateXml(xmlDocument, Optional.empty());
            assertEquals("""
                    <p lang="da">Det <b> nye </b> COM\u00ADpu\u00ADter</p>""", hyphenated);
        });
    }

    @org.junit.jupiter.api.Test
    void hyphenateDocument() throws ParserConfigurationException, TransformerException, IOException, SAXException, LanguageNotSupportedException {

        String xmlDocument = """
                <document>
                  <p lang="da">
                    I 1980'erne var en computer eller et <em>tv-spil</em> er en <span lang='en-US'>fantastic</span> ting at have.
                  </p>
                  <p lang="da">
                    I Polen havde de ikke computere i de små hjem i 1980'erne:
                    <span lang='pl'>
                      Stany Zjednoczone nałożyły sankcje gospodarcze na Polską Rzeczpospolitą Ludową:
                      Komputery  były dostępne tylko w dużych firmach.
                    </span>
                  </p>
                  <p>
                    In the 1980s, a computer or a video game was a fantastic thing to have.
                  </p>
                  <p lang='xy'>
                    In the 1980s, a computer or a video game was a fantastic thing to have.
                  </p>
                </document>
                """;
        var hyphenated = hyphenateXml(xmlDocument, Optional.of("en_US"));
        assertNotEquals(xmlDocument, hyphenated);
        assertTrue(hyphenated.contains("com\u00ADpu\u00ADte\u00ADre"));
    }

    @org.junit.jupiter.api.Test
    void hyphenateDocument_no_script() throws ParserConfigurationException, TransformerException, IOException, SAXException, LanguageNotSupportedException {
        String xmlDocument = """
                <html lang="en">
                  <head>
                    <style>
                      hyphen
                    </style>.
                    <script>
                      console.alert("Please choose some elongated words");
                    </script>.
                  </head>
                  <body>
                    <p>
                      This is just an example.
                    </p>
                  </body>
                </html>
                """;
        var hyphenated = hyphenateXml(xmlDocument, Optional.of("en_US"));
        //assertEquals(xmlDocument, hyphenated);
        assertFalse(hyphenated.contains("hy\u00ADphen"));
        assertFalse(hyphenated.contains("elon\u00ADgat\u00ADed"));
    }


    private static String hyphenateXml(String xmlDocument, Optional<String> defaultLanguage) throws SAXException, IOException, ParserConfigurationException, LanguageNotSupportedException, TransformerException {
        Document doc = DocumentBuilderFactory.newDefaultNSInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xmlDocument.getBytes(StandardCharsets.UTF_8)), "UTF-8");
        new XmlHyphenator().hyphenateDocument(doc, defaultLanguage);
        var sink = new ByteArrayOutputStream();
        var transformer = javax.xml.transform.TransformerFactory.newDefaultInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform
                (new DOMSource(doc), new StreamResult(sink));
        return sink.toString(StandardCharsets.UTF_8);
    }
}