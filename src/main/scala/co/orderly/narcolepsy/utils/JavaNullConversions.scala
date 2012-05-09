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

  // -------------------------------------------------------------------------------------------------------------------
  // From Scala Option[A]s to Java A-equivalents
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Convert a Scala Option[Float] to a Java Float.
   */
  implicit def optionalFloat2JFloat(float: Option[Float]): JFloat = float match {
    case None => null
    case Some(f) => f // Implicit Float -> JFloat happens here
  }

  /**
   * Convert a Scala Option[Int] to a Java Integer
   */
  implicit def optionalInt2JInteger(int: Option[Int]): JInteger = int match {
    case None => null
    case Some(i) => i // Implicit Int -> JInteger happens here
  }

  /**
   * Convert a Scala Option[Double] to a Java Double
   */
  implicit def optionalDouble2JDouble(double: Option[Double]): JDouble = double match {
    case None => null
    case Some(d) => d // Implicit Double -> JDouble happens here
  }

  // -------------------------------------------------------------------------------------------------------------------
  // From Java A-equivalents to Scala Option[A]s
  // -------------------------------------------------------------------------------------------------------------------

  /**
   * Convert a Java Float to a Scala Option[Float].
   *
   * Two-stage using scala's JavaConversions. Trying to do this in one
   * stage throws a:
   *   java.lang.NullPointerException
   *   at scala.Predef$.Float2float(Predef.scala:305)
   */
  implicit def jFloat2OptionalFloat(float: JFloat): Option[Float] =
    Option(float) map (f => f) // Implicit JFloat -> Float happens here

  /**
   * Convert a Java Integer to a Scala Option[Int]
   */
  implicit def jInteger2OptionalInt(int: JInteger): Option[Int] =
    Option(int) map (i => i) // Implicit JInteger -> Int happens here

  /**
   * Convert a Java Integer to a Scala Option[Int]
   */
  implicit def jDouble2OptionalDouble(double: JDouble): Option[Double] =
    Option(double) map (d => d) // Implicit JDouble -> Double happens here
}
