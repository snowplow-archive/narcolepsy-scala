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
package orderly.narcolepsy.marshallers.xml

// Java
import java.io.StringWriter
import java.io.StringReader
import java.text.SimpleDateFormat

// JAXB and XML
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter

// Narcolepsy
import orderly.narcolepsy.Representation

case class UnmarshalXml(xml: String) {

  /**
   * Turns the case class's xml into a Representation subclass.
   *
   * Example usage;
   * val order = UnmarshalXml(xml).toRepresentation[Order]
   */
  def toRepresentation[T <: Representation](implicit m: Manifest[T]): T =
    toRepresentation[T](m.erasure.asInstanceOf[Class[T]])

  /**
   * Turns the case class's xml into a Representation subclass - use
   * this form with an abstract Representation type, like so:
   *
   * val order = UnmarshalXml(xml).toRepresentation[T](typeOfT)
   *
   * (where you have grabbed and stored typeOfT using another
   * implicit Manifest at the point of declaring T.
   */
  def toRepresentation[T <: Representation](typeT: Class[T]): T =
    JAXBContext.newInstance(typeT).createUnmarshaller().unmarshal(
      new StringReader(xml)
    ).asInstanceOf[T]
}

trait XmlMarshaller {

  /**
   * Marshals this representation into XML
   */
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