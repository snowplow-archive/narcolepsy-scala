/*
 * Copyright (c) 2012 Orderly Ltd. All rights reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package co.orderly
package narcolepsy
package utils

// Java
import java.lang.{Float => JFloat}
import java.lang.{Integer => JInteger}
import java.lang.{Double => JDouble}

// Scala
import scala.collection.JavaConversions._

/**
 * Useful conversions between Scala Option[] values and nullable Java equivalents.
 * These conversions are required because Scala's .orNull and JavaConversions are
 * incompatible with each other
 */
object JavaNullConversions {

  /**
   * Convert a Scala Option[Float] to a Java Float.
   */
  implicit def optionalFloat2JFloat(float: Option[Float]): JFloat = float match {
    case None => null
    case Some(f) => f
  }

  /**
   * Convert a Scala Option[Int] to a Java Integer
   */
  implicit def optionalInt2JInteger(int: Option[Int]): JInteger = int match {
    case None => null
    case Some(i) => i
  }

  /**
   * Convert a Scala Option[Double] to a Java Double
   */
  implicit def optionalDouble2JDouble(double: Option[Double]): JDouble = double match {
    case None => null
    case Some(d) => d
  }
}
