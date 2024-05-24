package io.github.effiban.scala2java.core.testtrees

import scala.meta.{Type, XtensionQuasiquoteType}

/**
 * @deprecated use the corresponding fully-qualified type instead from [[io.github.effiban.scala2java.core.entities.TypeSelects]]
 */
@deprecated
object TypeNames {

  val Int: Type.Name = Type.Name("Int")
  val Byte: Type.Name = Type.Name("Byte")
  val Short: Type.Name = Type.Name("Short")
  val Long: Type.Name = Type.Name("Long")
  val Float: Type.Name = Type.Name("Float")
  val Double: Type.Name = Type.Name("Double")
  val Char: Type.Name = Type.Name("Char")
  val String: Type.Name = Type.Name("String")

  val Either: Type.Name = Type.Name("Either")
  val Try: Type.Name = Type.Name("Try")
  val Future: Type.Name = Type.Name("Future")

  val Stream: Type.Name = Type.Name("Stream")
  val List: Type.Name = Type.Name("List")
  val Seq: Type.Name = Type.Name("Seq")
  val Set: Type.Name = Type.Name("Set")
  val Map: Type.Name = Type.Name("Map")

  val Function: Type.Name = t"Function"

  val Throwable: Type.Name = t"Throwable"

  val Class: Type.Name = t"Class"

  val ScalaOption: Type.Name = Type.Name("Option")
  val ScalaAny: Type.Name = Type.Name("Any")
  val ScalaVector: Type.Name = Type.Name("Vector")
  val ScalaRange: Type.Name = Type.Name("Range")
}
