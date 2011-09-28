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

// JAXB and XML
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

/**
 * Representation is the parent class for all representations handled by
 * NarcolepsyClient. A representation is REST speak for the instantiated form
 * of a REST resource. For the purposes of Narcolepsy, a Representation is a
 * Scala class that has been marshalled from XML/JSON/whatever by JAXB, Jackson
 * or similar.
 */
class Representation {

  /**
   * Marshals this representation into XML
   */
  def marshallToXml(): String = {
    val context = JAXBContext.newInstance(this.getClass())
    val writer = new StringWriter
    context.createMarshaller.marshal(this, writer)

    writer.toString()
  }

  /**
   * Marshals this representation into JSON
   * Commented out until decide what to do with
   * this (but it's in the right place)
   */
  /*
  def marshalToJson[R <: Representation](representation: R, withRoot: Boolean = true) = {

    val mapper = new ObjectMapper()
    // Include (or not) a top level segment, as per http://stackoverflow.com/questions/5728276/jackson-json-top-level-segment-inclusion
    // We include if we are marshalling 1 entity (e.g. 1 platform), but exclude if marshalling N entities (e.g. N platforms)

    // TODO: I think we can change this to pattern match Representation vs RepresentationWrapper class
    mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, withRoot)

    val introspector = new JaxbAnnotationIntrospector();

    // TODO: the below has been deprecated. What's the new-new approach?
    // make deserializer use JAXB annotations (only)
    mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
    // make serializer use JAXB annotations (only)
    mapper.getSerializationConfig().setAnnotationIntrospector(introspector);

    val writer = mapper.defaultPrettyPrintingWriter
    writer.writeValueAsString(representation)
  } */
}