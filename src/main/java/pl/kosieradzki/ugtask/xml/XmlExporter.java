package pl.kosieradzki.ugtask.xml;

import pl.kosieradzki.ugtask.model.Computer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class XmlExporter {

    public void export(List<Computer> computers, Path outputFile) throws IOException, XMLStreamException {
        Files.createDirectories(outputFile.toAbsolutePath().getParent());

        try (Writer fileWriter = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
            XMLStreamWriter xml = XMLOutputFactory.newFactory().createXMLStreamWriter(fileWriter);
            try {
                xml.writeStartDocument("UTF-8", "1.0");
                xml.writeCharacters("\n");
                xml.writeStartElement("faktura");

                for (Computer computer : computers) {
                    xml.writeCharacters("\n  ");
                    xml.writeStartElement("komputer");
                    writeElement(xml, "nazwa", computer.name());
                    writeElement(xml, "data_ksiegowania", computer.accountingDate().toString());
                    writeElement(xml, "koszt_USD", computer.costUSD().toPlainString());
                    writeElement(xml, "koszt_PLN", computer.costPLN().toPlainString());
                    xml.writeCharacters("\n  ");
                    xml.writeEndElement();
                }

                xml.writeCharacters("\n");
                xml.writeEndElement();
                xml.writeEndDocument();
                xml.flush();
            } finally {
                xml.close();
            }
        }
    }

    private void writeElement(XMLStreamWriter xml, String name, String value)
            throws XMLStreamException {
        xml.writeCharacters("\n    ");
        xml.writeStartElement(name);
        xml.writeCharacters(value);
        xml.writeEndElement();
    }
}
