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
package orderly.narcolepsy.representations

// Scala
import scala.reflect.BeanProperty

// JAXB
import javax.xml.bind.annotation._

// Jackson
import org.codehaus.jackson.annotate._

/**
 * Singleton to construct an AtomLink. (Content) type defaults
 * to application/xml if not explicitly set
 */
object AtomLink {

  /**
   * Convenience constructor for an AtomLink
   */
  def apply(
    rel:  String,
    href: String,
    `type`: String = "application/xml"): AtomLink = {

    val al = new AtomLink
    al.rel = rel
    al.href = href
    al.`type` = `type`

    al // Return the new AtomLink bean
  }
}

/**
 * AtomLink is used to define a HATEOAS-friendly URL to access the detail
 * on a specific resource. For more on this see this excellent blog post:
 * http://www.zienit.nl/blog/2010/01/rest/hateoas-by-example
 */
@XmlRootElement(name="link") //, namespace="http://www.w3.org/2005/Atom")
@JsonProperty("link")
@XmlAccessorType(XmlAccessType.FIELD)
class AtomLink {

  @XmlAttribute
  @BeanProperty
  var rel: String = _

  @XmlAttribute
  @BeanProperty
  var href: String = _

  @XmlAttribute
  @BeanProperty
  var `type`: String = _
}