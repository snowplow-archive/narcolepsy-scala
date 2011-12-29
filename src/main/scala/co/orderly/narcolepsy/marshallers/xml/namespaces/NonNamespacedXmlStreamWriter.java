/*
 * Copyright (c) 2011 Orderly Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package co.orderly.narcolepsy.marshallers.xml.namespaces;

// JAXB
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

// TODO: port this to Scala

public class NonNamespacedXmlStreamWriter implements XMLStreamWriter {

    private XMLStreamWriter xmlStreamWriter;

    public NonNamespacedXmlStreamWriter(XMLStreamWriter xmlStreamWriter) {
        this.xmlStreamWriter = xmlStreamWriter;
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(localName);
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(namespaceURI, localName);
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(prefix, localName, namespaceURI);
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(namespaceURI, localName);
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(prefix, localName, namespaceURI);
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(localName);
    }

    public void writeEndElement() throws XMLStreamException {
        xmlStreamWriter.writeEndElement();
    }

    public void writeEndDocument() throws XMLStreamException {
        xmlStreamWriter.writeEndDocument();
    }

    public void close() throws XMLStreamException {
        xmlStreamWriter.close();
    }

    public void flush() throws XMLStreamException {
        xmlStreamWriter.flush();
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(localName, value);
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        if("http://www.w3.org/2001/XMLSchema-instance".equals(namespaceURI)) {
            int colonIndex = value.indexOf(':');
            if(colonIndex > -1) {
                value = value.substring(colonIndex + 1);
            }
            xmlStreamWriter.writeAttribute(localName, value);
        } else {
            xmlStreamWriter.writeAttribute(prefix, namespaceURI, localName, value);
        }
    }

    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        if("http://www.w3.org/2001/XMLSchema-instance".equals(namespaceURI)) {
            int colonIndex = value.indexOf(':');
            if(colonIndex > -1) {
                value = value.substring(colonIndex + 1);
            }
            xmlStreamWriter.writeAttribute(localName, value);
        } else {
            xmlStreamWriter.writeAttribute(namespaceURI, localName, value);
        }
    }

    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        if(!"http://www.w3.org/2001/XMLSchema-instance".equals(namespaceURI) && !"http://www.w3.org/2001/XMLSchema".equals(namespaceURI)) {
            xmlStreamWriter.writeNamespace(prefix, namespaceURI);
        }
    }

    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        if(!"http://www.w3.org/2001/XMLSchema-instance".equals(namespaceURI)) {
            xmlStreamWriter.writeDefaultNamespace(namespaceURI);
        }
    }

    public void writeComment(String data) throws XMLStreamException {
        // TODO Auto-generated method stub
    }

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        // TODO Auto-generated method stub
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        // TODO Auto-generated method stub
    }

    public void writeCData(String data) throws XMLStreamException {
        // TODO Auto-generated method stub
    }

    public void writeDTD(String dtd) throws XMLStreamException {
        // TODO Auto-generated method stub
    }

    public void writeEntityRef(String name) throws XMLStreamException {
        // TODO Auto-generated method stub
    }

    public void writeStartDocument() throws XMLStreamException {
        xmlStreamWriter.writeStartDocument();
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        xmlStreamWriter.writeStartDocument(version);
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        xmlStreamWriter.writeStartDocument(encoding, version);
    }

    public void writeCharacters(String text) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(text);
    }

    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(text, start, len);
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return xmlStreamWriter.getPrefix(uri);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        xmlStreamWriter.setPrefix(prefix, uri);
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        xmlStreamWriter.setDefaultNamespace(uri);
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        xmlStreamWriter.setNamespaceContext(context);
    }

    public NamespaceContext getNamespaceContext() {
        return xmlStreamWriter.getNamespaceContext();
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return xmlStreamWriter.getProperty(name);
    }

}