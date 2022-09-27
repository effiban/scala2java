package effiban.scala2java.testtrees

import scala.meta.Term

object TermNames {

  val ScalaTermName: Term.Name = Term.Name("scala")

  val ScalaRangeTermName: Term.Name = Term.Name("Range")
  val ScalaToTermName: Term.Name = Term.Name("to")
  val ScalaUntilTermName: Term.Name = Term.Name("until")
  val ScalaInclusiveTermName: Term.Name = Term.Name("inclusive")

  val JavaIntStreamTermName: Term.Name = Term.Name("IntStream")
  val JavaRangeTermName: Term.Name = Term.Name("range")
  val JavaRangeClosedTermName: Term.Name = Term.Name("rangeClosed")

  val Array: Term.Name = Term.Name("Array")
  val List: Term.Name = Term.Name("List")
  val Vector: Term.Name = Term.Name("Vector")
  val Seq: Term.Name = Term.Name("Seq")
  val Set: Term.Name = Term.Name("Set")
  val Map: Term.Name = Term.Name("Map")

  val String: Term.Name = Term.Name("String")

  val PlusTermName: Term.Name = Term.Name("+")
}
