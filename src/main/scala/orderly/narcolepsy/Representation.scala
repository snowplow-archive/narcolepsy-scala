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
package orderly.narcolepsy

// Java
import java.io.StringWriter
import java.io.StringReader
import java.text.SimpleDateFormat

// JAXB and XML
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

// Jackson
import org.codehaus.jackson.map._
import org.codehaus.jackson.map.introspect._
import org.codehaus.jackson.xc._

case class UnmarshalXml(xml: String) {

  def toRepresentation[R <: Representation](implicit m: Manifest[R]): R = {
    JAXBContext.newInstance(m.erasure.asInstanceOf[Class[R]]).createUnmarshaller().unmarshal(
      new StringReader(xml)
    ).asInstanceOf[R]
  }
}

case class UnmarshalJson(json: String) {

  def toRepresentation[R <: Representation](implicit m: Manifest[R]): R = {

    val mapper = new ObjectMapper()
    mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true)
    mapper.getDeserializationConfig().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))

    // Use Jackson annotations but fall back to JAXB
    val introspectorPair = new AnnotationIntrospector.Pair(
      new JacksonAnnotationIntrospector(),
      new JaxbAnnotationIntrospector()
    )
    mapper.getDeserializationConfig().withAnnotationIntrospector(introspectorPair)

    // Return the representation
    mapper.readValue(json, m.erasure).asInstanceOf[R]
  }
}

/**
 * Representation is the parent class for all representations handled by
 * NarcolepsyClient. A representation is REST speak for the instantiated form
 * of a REST resource. For the purposes of Narcolepsy, a Representation is a
 * Scala class that has been marshalled from XML/JSON/whatever by JAXB, Jackson
 * or similar.
 */
abstract class Representation {

  /**
   * Marshals this representation into XML
   */
  def marshalToXml(): String = {
    val context = JAXBContext.newInstance(this.getClass())
    val writer = new StringWriter
    context.createMarshaller.marshal(this, writer)

    writer.toString()
  }

  /**
   * Marshals this representation into JSON via Jackson
   * (using JAXB annotations)
   */
  def marshalToJson(): String = {

    // Define the Jackson mapper and configure it
    val mapper = new ObjectMapper()

    // Determine whether we should be showing a root value, aka a "top level segment",
    // as per http://stackoverflow.com/questions/5728276/jackson-json-top-level-segment-inclusion
    val rootKey = this match {
      case r:RepresentationWrapper => false // Don't include as we get the root key for free
      case _ => true // Yes include a root key
    }
    mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, rootKey)

    // Translates typical camel case Java property names to lower case JSON element names, separated by underscore
    mapper.setPropertyNamingStrategy(new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy())

    // Use Jackson annotations first, fall back to JAXB ones
    val introspectorPair = new AnnotationIntrospector.Pair(
      new JacksonAnnotationIntrospector(),
      new JaxbAnnotationIntrospector()
    )

    mapper.getSerializationConfig().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
    mapper.getSerializationConfig().withAnnotationIntrospector(introspectorPair)

    val writer = mapper.defaultPrettyPrintingWriter
    writer.writeValueAsString(this)
  }
}