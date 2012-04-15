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

/**
 * JaxbConfiguration allows the Jaxb marshalling and unmarshalling
 * to be tweaked/customized for a given API.
 */
case class JaxbConfiguration(namespaced: Boolean)

/**
 * Case class mini-DSL for unmarshalling via JAXB.
 *
 * Design as per Neil Essy's answer on:
 * http://stackoverflow.com/questions/8162345/how-do-i-create-a-class-hierarchy-of-typed-factory-method-constructors-and-acces
 */
case class JaxbUnmarshaller(conf: JaxbConfiguration) extends Unmarshaller {

  /**
   * Turns the case class's xml into a Representation subclass - use
   * this form with an abstract Representation type, like so:
   *
   * val order = UnmarshalXml(xml).toRepresentation[R](typeOfR)
   *
   * (where you have grabbed and stored typeOfR using another
   * implicit Manifest at the point of declaring R.
   */
  def toRepresentation[R <: Representation](marshalled: String, typeR: Class[R]): R = {

    // TODO: need to add non-namespaced support in (although not sure it is strictly necessary - seems to work fine without)

    val context = JAXBContext.newInstance(typeR)

    val unmarshaller = context.createUnmarshaller()

    unmarshaller.unmarshal(
      new StringReader(marshalled)
    ).asInstanceOf[R]
  }
}

/**
 * Case class mini-DSL for marshalling via JAXB.
 */
case class JaxbMarshaller(conf: JaxbConfiguration) extends Marshaller {

  /**
   * Marshals this representation into XML using JAXB
   */
  def fromRepresentation[R <: Representation](representation: R): String = {

    val context = JAXBContext.newInstance(this.getClass())
    val writer = new StringWriter

    if (conf.namespaced) {
      context.createMarshaller.marshal(this, writer)
    } else { // Use the custom NonNamespacedXmlStreamWriter to produce XML without the namespace noise everywhere
      val xof = XMLOutputFactory.newFactory()
      val xsw = xof.createXMLStreamWriter(writer)
      context.createMarshaller.marshal(representation, new NonNamespacedXmlStreamWriter(xsw))
    }

    writer.toString()
  }
}
