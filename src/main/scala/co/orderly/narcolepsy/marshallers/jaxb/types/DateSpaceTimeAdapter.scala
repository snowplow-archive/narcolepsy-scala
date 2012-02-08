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
package co.orderly.narcolepsy.marshallers.jaxb.types

// Java
import java.text.SimpleDateFormat
import java.util.{Date => JDate}

// JAXB
import javax.xml.bind.annotation.adapters.XmlAdapter

/**
 * DateSpaceTimeAdapter is for handling datetimes in the format yyyy-MM-dd HH:mm:ss.
 *
 * This is useful because by default JAXB expects datetimes to be in the format
 * yyyy-MM-ddTHH:mm:ss (note the 'T' in the middle)
 */
class DateSpaceTimeAdapter extends XmlAdapter[String, JDate] {

  private val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  @throws(classOf[Exception])
  override def marshal(value: JDate): String = dateFormat.format(value)

  @throws(classOf[Exception])
  override def unmarshal(value: String): JDate = Option(value) match {
    case Some(date) if date != "0000-00-00 00:00:00" => dateFormat.parse(date)
    case _ => null
  }
}
