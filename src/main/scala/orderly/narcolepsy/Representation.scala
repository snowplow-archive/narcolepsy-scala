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

    // First determine whether we should be showing a root value, aka a "top level segment",
    // as per http://stackoverflow.com/questions/5728276/jackson-json-top-level-segment-inclusion
    /* val wrapRootValue = this match {
      case r:RepresentationWrapper => true // Yes include a root value wrapper
      case _ => false
    } */

    // Define the Jackson mapper and configure it
    val om = new ObjectMapper()

    // mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, false)

    val introspectorPair = new AnnotationIntrospector.Pair(
      new JacksonAnnotationIntrospector(),
      new JaxbAnnotationIntrospector()
    )
    om.getSerializationConfig().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
    om.getSerializationConfig().withAnnotationIntrospector(introspectorPair)

    val writer = om.defaultPrettyPrintingWriter
    writer.writeValueAsString(this)
  }
}