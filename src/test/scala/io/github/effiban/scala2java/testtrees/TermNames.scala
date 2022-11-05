package io.github.effiban.scala2java.testtrees

import scala.meta.Term

object TermNames {

  val String: Term.Name = Term.Name("String")
  val Either: Term.Name = Term.Name("Either")
  val LowercaseRight: Term.Name = Term.Name("right")
  val LowercaseLeft: Term.Name = Term.Name("left")
  val Try: Term.Name = Term.Name("Try")
  val Future: Term.Name = Term.Name("Future")
  val Stream: Term.Name = Term.Name("Stream")
  val List: Term.Name = Term.Name("List")
  val Vector: Term.Name = Term.Name("Vector")
  val Seq: Term.Name = Term.Name("Seq")
  val Set: Term.Name = Term.Name("Set")
  val Map: Term.Name = Term.Name("Map")

  val Plus: Term.Name = Term.Name("+")

  val Apply: Term.Name = Term.Name("apply")
  val Empty: Term.Name = Term.Name("empty")

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
  val ScalaNil: Term.Name = Term.Name("Nil")

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
