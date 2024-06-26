package io.github.effiban.scala2java.core.testtrees

import scala.meta.{Term, XtensionQuasiquoteTerm}

/**
 * @deprecated use [[io.github.effiban.scala2java.core.entities.TermNames]] instead but copy only what is really used in production
 */
@deprecated
object TermNames {

  val String: Term.Name = Term.Name("String")
  val Either: Term.Name = Term.Name("Either")
  val LowercaseRight: Term.Name = Term.Name("right")
  val LowercaseLeft: Term.Name = Term.Name("left")
  val Try: Term.Name = Term.Name("Try")
  val Future: Term.Name = Term.Name("Future")
  val Stream: Term.Name = Term.Name("Stream")
  val List: Term.Name = Term.Name("List")
  val Seq: Term.Name = Term.Name("Seq")
  val Set: Term.Name = Term.Name("Set")
  val Map: Term.Name = Term.Name("Map")

  val Plus: Term.Name = Term.Name("+")
  val Minus: Term.Name = Term.Name("-")
  val Multiply: Term.Name = Term.Name("*")
  val Divide: Term.Name = Term.Name("/")
  val Modulus: Term.Name = Term.Name("%")
  val And: Term.Name = Term.Name("&&")
  val Or: Term.Name = Term.Name("||")
  val BitwiseAnd: Term.Name = Term.Name("&")
  val BitwiseOr: Term.Name = Term.Name("|")
  val BitwiseXor: Term.Name = Term.Name("^")
  val Equals: Term.Name = Term.Name("==")
  val NotEquals: Term.Name = Term.Name("!=")
  val GreaterThan: Term.Name = Term.Name(">")
  val GreaterEquals: Term.Name = Term.Name(">=")
  val LessThan: Term.Name = Term.Name("<")
  val LessEquals: Term.Name = Term.Name("<=")

  val Apply: Term.Name = Term.Name("apply")
  val Empty: Term.Name = Term.Name("empty")
  val AndThen: Term.Name = Term.Name("andThen")
  val Print: Term.Name = q"print"
  val Println: Term.Name = q"println"

  val Scala: Term.Name = Term.Name("scala")
  val ScalaRange: Term.Name = Term.Name("Range")
  val ScalaTo: Term.Name = Term.Name("to")
  val ScalaUntil: Term.Name = Term.Name("until")
  val ScalaInclusive: Term.Name = Term.Name("inclusive")
  val ScalaAssociation: Term.Name = Term.Name("->")
  val ScalaOption: Term.Name = Term.Name("Option")
  val ScalaArray: Term.Name = Term.Name("Array")
  val ScalaSome: Term.Name = Term.Name("Some")
  val ScalaNone: Term.Name = Term.Name("None")
  val ScalaRight: Term.Name = Term.Name("Right")
  val ScalaLeft: Term.Name = Term.Name("Left")
  val ScalaSuccess: Term.Name = Term.Name("Success")
  val ScalaFailure: Term.Name = Term.Name("Failure")
  val ScalaSuccessful: Term.Name = Term.Name("successful")
  val ScalaFailed: Term.Name = Term.Name("failed")
  val ScalaVector: Term.Name = Term.Name("Vector")
  val ScalaNil: Term.Name = Term.Name("Nil")
  val ScalaClassOf: Term.Name = Term.Name("classOf")

  val Java: Term.Name = Term.Name("java")
  val JavaIntStream: Term.Name = Term.Name("IntStream")
  val JavaRange: Term.Name = Term.Name("range")
  val JavaRangeClosed: Term.Name = Term.Name("rangeClosed")
  val JavaEntryMethod: Term.Name = Term.Name("entry")
  val JavaOptional: Term.Name = Term.Name("Optional")
  val JavaOf: Term.Name = Term.Name("of")
  val JavaOfNullable: Term.Name = Term.Name("ofNullable")
  val JavaAbsent: Term.Name = Term.Name("absent")
  val JavaOfEntries: Term.Name = Term.Name("ofEntries")
  val JavaOfSupplier: Term.Name = Term.Name("ofSupplier")
  val JavaCompletableFuture: Term.Name = Term.Name("CompletableFuture")
  val JavaSupplyAsync: Term.Name = Term.Name("supplyAsync")
  val JavaCompletedFuture: Term.Name = Term.Name("completedFuture")
  val JavaFailedFuture: Term.Name = Term.Name("failedFuture")
  val JavaSuccess: Term.Name = Term.Name("success")
  val JavaFailure: Term.Name = Term.Name("failure")

}
