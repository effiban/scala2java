package effiban.scala2java.testtrees

import scala.meta.Type

object TypeNames {

  val Int: Type.Name = Type.Name("Int")
  val Long: Type.Name = Type.Name("Long")
  val Double: Type.Name = Type.Name("Double")
  val String: Type.Name = Type.Name("String")
  val Unit: Type.Name = Type.Name("Unit")

  val Either: Type.Name = Type.Name("Either")
  val Try: Type.Name = Type.Name("Try")
  val Future: Type.Name = Type.Name("Future")

  val Stream: Type.Name = Type.Name("Stream")
  val List: Type.Name = Type.Name("List")
  val Seq: Type.Name = Type.Name("Seq")
  val Set: Type.Name = Type.Name("Set")
  val Map: Type.Name = Type.Name("Map")

  val ScalaArray: Type.Name = Type.Name("Array")
  val ScalaOption: Type.Name = Type.Name("Option")
  val ScalaAny: Type.Name = Type.Name("Any")
  val ScalaVector: Type.Name = Type.Name("Vector")
}
