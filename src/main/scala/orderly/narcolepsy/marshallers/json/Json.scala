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
package orderly.narcolepsy.marshallers.json

// Jackson
import org.codehaus.jackson.map._
import org.codehaus.jackson.map.introspect._
import org.codehaus.jackson.xc._

// Narcolepsy
import orderly.narcolepsy._

/**
 * Mini-DSL to unmarshal a JSON string into a Representation.
 *
 * Design as per Neil Essy's answer on:
 * http://stackoverflow.com/questions/8162345/how-do-i-create-a-class-hierarchy-of-typed-factory-method-constructors-and-acces
 */
case class UnmarshalJson(json: String) {

  def toRepresentation[T <: Representation](implicit m: Manifest[T]): T =
   toRepresentation[T](m.erasure.asInstanceOf[Class[T]])

  def toRepresentation[T <: Representation](typeT: Class[T]): T = {

    // Define the Jackson mapper and configure it
    val mapper = new ObjectMapper()
    mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, needRootKey(this))
    mapper.getDeserializationConfig().setDateFormat(getDateFormat)

    // Translates typical camel case Java property names to lower case JSON element names, separated by underscore
    mapper.setPropertyNamingStrategy(new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy())

    // Use Jackson annotations first then fall back on JAXB annotations
    val introspectorPair = new AnnotationIntrospector.Pair(
      new JacksonAnnotationIntrospector(),
      new JaxbAnnotationIntrospector()
    )
    mapper.getDeserializationConfig().withAnnotationIntrospector(introspectorPair)

    // Return the representation
    mapper.readValue(json, typeT).asInstanceOf[T]
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
    mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, needRootKey(this))
    mapper.getSerializationConfig().setDateFormat(getDateFormat)

    // Translates typical camel case Java property names to lower case JSON element names, separated by underscore
    mapper.setPropertyNamingStrategy(new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy())

    // Use Jackson annotations first then fall back on JAXB annotations
    val introspectorPair = new AnnotationIntrospector.Pair(
      new JacksonAnnotationIntrospector(),
      new JaxbAnnotationIntrospector()
    )
    mapper.getSerializationConfig().withAnnotationIntrospector(introspectorPair)

    val writer = mapper.defaultPrettyPrintingWriter
    writer.writeValueAsString(this)
  }
}