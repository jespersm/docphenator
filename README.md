```markdown
# DocPhenator

DocPhenator is a Java library designed to hyphenate text within XML documents. It leverages hyphenation patterns to insert soft hyphens into words, making text more readable when rendered in narrow columns.

## Features

- Hyphenates text within XML documents.
- Supports multiple languages.
- Easy integration with existing Java projects using Maven.

## Installation

To use DocPhenator in your project, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.jespersm</groupId>
    <artifactId>docphenator</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Usage

Here is an example of how to use DocPhenator to hyphenate an XML document:

```java
import io.github.jespersm.docphenator.XmlHyphenator;
import org.w3c.dom.Document;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Example {
    public static void main(String[] args) throws Exception {
        String xmlDocument = """
                <document lang="da">
                  <p>
                    I Polen havde de ikke computere i de små hjem i 1980'erne:
                    <span lang='pl'>
                      Stany Zjednoczone nałożyły sankcje gospodarcze na Polską Rzeczpospolitą Ludową:
                      Komputery były dostępne tylko w dużych firmach.
                    </span>
                  </p>
                  <p lang='xy'><!-- Unknown language: will be ignored -->
                    In the 1980s, a computer or a video game was a fantastic thing to have.
                  </p>
                </document>
                """;

        Document doc = DocumentBuilderFactory.newDefaultNSInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xmlDocument.getBytes(StandardCharsets.UTF_8)), "UTF-8");

        new XmlHyphenator().hyphenateDocument(doc, Optional.of("da"));

        var sink = new ByteArrayOutputStream();
        javax.xml.transform.TransformerFactory.newDefaultInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(sink));
        var hyphenated = sink.toString(StandardCharsets.UTF_8);

        System.out.println(hyphenated);
    }
}
```

## License

This project is licensed under the LGPL License. See the `LICENSE` file for details.

## Acknowledgements

- Hyphenation patterns are sourced from [hyphenation.org](https://hyphenation.org).
- The [Hypenator](https://github.com/Nianna/hyphenator) is used to hyphenate text.

```
