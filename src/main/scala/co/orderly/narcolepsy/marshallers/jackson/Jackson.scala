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
 * Specify the different strategies for including a root value with a given representation.
 */
object RootValueStrategy extends Enumeration {
  type RootValueStrategy = Value
  val All, NotWrappers, None = Value
}
import RootValueStrategy._

/**
 * JacksonConfiguration allows the Jackson marshalling and unmarshalling
 * to be tweaked/customized for a given API.
 */
case class JacksonConfiguration(dateFormat: SimpleDateFormat,
                                rootValueStrategy: RootValueStrategy,
                                propertyNamingStrategy: PropertyNamingStrategy) 

/**
 * Mini-DSL to unmarshal a JSON string into a Representation.
 *
 * Design as per Neil Essy's answer on:
 * http://stackoverflow.com/questions/8162345/how-do-i-create-a-class-hierarchy-of-typed-factory-method-constructors-and-acces
 */
case class JacksonUnmarshaller(conf: JacksonConfiguration) extends Unmarshaller with JacksonHelpers {

  def toRepresentation[R <: Representation](marshalled: String, typeR: Class[R]): R = {

    val (mapper, ai) = createObjectMapperAndIntrospector(conf)

    mapper.getDeserializationConfig().withAnnotationIntrospector(ai)
    mapper.getDeserializationConfig().setDateFormat(conf.dateFormat) // TODO: setDateFormat has been deprecated

    // Whether or not to add a root key aka "top level segment" when (un)marshalling JSON, as
    // per http://stackoverflow.com/questions/5728276/jackson-json-top-level-segment-inclusion
    // Unmarshalling only
    mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, unwrapRootValue(conf.rootValueStrategy, typeR))

    // Return the representation
    mapper.readValue(marshalled, typeR).asInstanceOf[R]
  }
}

/**
 * Case class mini-DSL for marshalling via Jackson.
 */
case class JacksonMarshaller(conf: JacksonConfiguration) extends Marshaller with JacksonHelpers {

  /**
   * Marshals this representation into JSON via Jackson
   */
  def fromRepresentation[R <: Representation](representation: R): String = {

    val (mapper, ai) = createObjectMapperAndIntrospector(conf)

    mapper.getSerializationConfig().withAnnotationIntrospector(ai)
    mapper.getSerializationConfig().setDateFormat(conf.dateFormat) // TODO: setDateFormat has been deprecated

    // Return a pretty printed String
    val writer = mapper.defaultPrettyPrintingWriter // Deprecated, replace
    writer.writeValueAsString(representation)
  }
}

/**
 * Helpers used by Jackson for both marshalling and unmarshalling.
 */
trait JacksonHelpers {

  /**
   * Factory to create and configure a Jackson ObjectMapper based on
   * the supplied configuration. Same for marshalling and
   * unmarshalling.
   */
  def createObjectMapperAndIntrospector(conf: JacksonConfiguration): (ObjectMapper, AnnotationIntrospector) = {

    val mapper = new ObjectMapper()

    // How to name the properties (e.g. lower case with underscores)
    mapper.setPropertyNamingStrategy(conf.propertyNamingStrategy)

    // Use Jackson annotations first then fall back on JAXB annotations
    // TODO: make this into a FallbackStrategy
    val introspectorPair = new AnnotationIntrospector.Pair(
      new JacksonAnnotationIntrospector(),
      new JaxbAnnotationIntrospector()
    )

    (mapper, introspectorPair) // Return the tuple
  }

  /**
   * Whether to set unwrap root value to true or false
   */
  def unwrapRootValue[R <: Representation](rvs: RootValueStrategy, typeR: Class[R]): Boolean = rvs match {
    case All         => true
    case None        => false
    case NotWrappers => !isWrapper(typeR)
  }

  /**
   * Uses reflection to determine whether a given reified type subclasses
   * RepresentationWrapper or not. Used to help determine whether Jackson
   * should be setting a root key or not.
   */
  private def isWrapper(typeR: Class[_]) = classOf[RepresentationWrapper[_]].isAssignableFrom(typeR)
}
