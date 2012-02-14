/*
 * Copyright (c) 2012 Orderly Ltd. All rights reserved.
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
package co.orderly.narcolepsy
package marshallers
package jaxb

// Java
import java.io.StringWriter
import java.io.StringReader
import java.text.SimpleDateFormat

// XML
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter

// JAXB
import javax.xml.bind.JAXBContext

// Narcolepsy
import namespaces.NonNamespacedXmlStreamWriter

case class UnmarshalXml(xml: String) extends Unmarshaller {

  /**
   * Turns the case class's xml into a Representation subclass - use
   * this form with an abstract Representation type, like so:
   *
   * val order = UnmarshalXml(xml).toRepresentation[T](typeOfT)
   *
   * (where you have grabbed and stored typeOfT using another
   * implicit Manifest at the point of declaring T.
   */
  def toRepresentation[T <: Representation](typeT: Class[T]): T = {

    // TODO: need to add non-namespaced support in (although not sure
    // it is strictly necessary - seems to work fine without)

    val context = JAXBContext.newInstance(typeT)

    val unmarshaller = context.createUnmarshaller()

    unmarshaller.unmarshal(
      new StringReader(xml)
    ).asInstanceOf[T]
  }
}

trait JaxbMarshaller extends Marshaller {

  /**
   * Marshals this representation into XML using JAXB
   */
  // TODO: rename this back to marshal() when it's no longer attached to all Representations
  // TODO: move namespaced into a separate configuration object or similar
  def marshalToXml(namespaced: Boolean = true): String = {

    val context = JAXBContext.newInstance(this.getClass())
    val writer = new StringWriter

    if (namespaced) {
      context.createMarshaller.marshal(this, writer)
    } else { // Use the custom NonNamespacedXmlStreamWriter to produce XML without the namespace noise everywhere
      val xof = XMLOutputFactory.newFactory()
      val xsw = xof.createXMLStreamWriter(writer)
      context.createMarshaller.marshal(this, new NonNamespacedXmlStreamWriter(xsw))
    }

    writer.toString()
  }
}