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
package jackson

// Java
import java.text.SimpleDateFormat

// Jackson
import org.codehaus.jackson.map._
import org.codehaus.jackson.map.introspect._
import org.codehaus.jackson.xc._

/**
 * Mini-DSL to unmarshal a JSON string into a Representation.
 *
 * Design as per Neil Essy's answer on:
 * http://stackoverflow.com/questions/8162345/how-do-i-create-a-class-hierarchy-of-typed-factory-method-constructors-and-acces
 */
case class UnmarshalJson(json: String, rootKey: Boolean = false) extends Unmarshaller {

  def toRepresentation[T <: Representation](typeT: Class[T]): T = {

    // Define the Jackson mapper and configure it
    val mapper = new ObjectMapper()

    // Determine if we are unmarshalling a RepresentationWrapper subclass or not
    // TODO: this isn't working yet
    val isWrapper = typeT match {
      case x:Class[RepresentationWrapper[_]] => true
      case _ => false
    }

    // TODO: turn these into JacksonConfiguration-based or similar
    mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, (rootKey && (!isWrapper)))
    // mapper.getDeserializationConfig().setDateFormat(getDateFormat)

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

trait JacksonMarshaller extends Marshaller {

  /**
   * Marshals this representation into JSON via Jackson
   * (using Jackson / JAXB annotations)
   */
  def marshal(): String = {

    // Define the Jackson mapper and configure it
    val mapper = new ObjectMapper()

    // TODO: we need to inject a JacksonConfiguration into this OR make it easy to override JacksonMarshaller
    // TODO in an individual Narcolepsy client
    // mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, needRootKey(this))
    // mapper.getSerializationConfig().setDateFormat(getDateFormat)

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

/* Archive of JSONy unmarshalling stuff


  // -------------------------------------------------------------------------------------------------------------------
  // Helper methods
  // -------------------------------------------------------------------------------------------------------------------

  // TODO: check if this is is still used
  // TODO: make this a JSON configuration parameter
  /**
   * Whether or not to add a root key aka "top level segment" when (un)marshalling JSON, as
   * per http://stackoverflow.com/questions/5728276/jackson-json-top-level-segment-inclusion
   */
  def needRootKey(obj: Any) = obj match {
    case o:RepresentationWrapper[_] => false // Don't include as we get the root key for free with a wrapper
    case _ => true                        // Yes include a root key
  }

  // TODO: make this a JSON configuration parameter to the client (along with needRootKey)
  /**
   * Standardise the date format to use for (un)marshalling
   */
  def getDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")


*/