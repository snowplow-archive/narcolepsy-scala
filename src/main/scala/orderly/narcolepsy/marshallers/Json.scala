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
package orderly.narcolepsy.marshallers

// Java
import java.text.SimpleDateFormat

// Jackson
import org.codehaus.jackson.map._
import org.codehaus.jackson.map.introspect._
import org.codehaus.jackson.xc._

// Narcolepsy
import orderly.narcolepsy.{Representation, RepresentationWrapper}

/**
 * Mini-DSL to unmarshal a JSON string into a Representation.
 *
 * Design as per Neil Essy's answer on:
 * http://stackoverflow.com/questions/8162345/how-do-i-create-a-class-hierarchy-of-typed-factory-method-constructors-and-acces
 */
case class UnmarshalJson(json: String) {

  def toRepresentation[T <: Representation](implicit m: Manifest[T]): T = {

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
    mapper.readValue(json, m.erasure).asInstanceOf[T]
  }
}

trait JsonMarshaller {

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