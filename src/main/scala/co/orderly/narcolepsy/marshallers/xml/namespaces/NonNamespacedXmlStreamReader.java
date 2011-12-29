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
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

// TODO: port this to Scala

public class NonNamespacedXmlStreamReader extends StreamReaderDelegate {

    public NonNamespacedXmlStreamReader(XMLStreamReader xmlStreamReader) {
        super(xmlStreamReader);
    }

    @Override
    public String getAttributeNamespace(int index) {
        String attributeName = getAttributeLocalName(index);
        if("type".equals(attributeName) || "nil".equals(attributeName)) {
            return "http://www.w3.org/2001/XMLSchema-instance";
        }
        return super.getAttributeNamespace(index);
    }


}