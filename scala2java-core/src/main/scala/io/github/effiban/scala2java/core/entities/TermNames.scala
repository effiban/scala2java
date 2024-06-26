package io.github.effiban.scala2java.core.entities

import scala.meta.{Term, XtensionQuasiquoteTerm}

object TermNames {

  final val Future: Term.Name = q"Future"
  final val Either: Term.Name = q"Either"
  final val LowercaseRight: Term.Name = q"right"
  final val LowercaseLeft: Term.Name = q"left"
  final val Stream: Term.Name = q"Stream"
  final val List: Term.Name = q"List"
  final val Seq: Term.Name = q"Seq"
  final val Set: Term.Name = q"Set"
  final val Map: Term.Name = q"Map"
  final val Try: Term.Name = q"Try"
  final val Apply: Term.Name = q"apply"
  final val Empty: Term.Name = q"empty"
  final val Util: Term.Name = q"util"

  final val Plus: Term.Name = q"+"
  final val Minus: Term.Name = q"-"
  final val Multiply: Term.Name = q"*"
  final val Divide: Term.Name = q"/"
  final val Modulus: Term.Name = q"%"

  final val And: Term.Name = q"&&"
  final val Or: Term.Name = q"||"

  final val BitwiseAnd: Term.Name = q"&"
  final val BitwiseOr: Term.Name = q"|"
  final val BitwiseXor: Term.Name = q"^"

  final val Equals: Term.Name = q"=="
  final val NotEquals: Term.Name = q"!="
  final val GreaterThan: Term.Name = q">"
  final val GreaterEquals: Term.Name = q">="
  final val LessThan: Term.Name = q"<"
  final val LessEquals: Term.Name = q"<="

  final val AndThen: Term.Name = q"andThen"
  final val Compose: Term.Name = q"compose"
  final val Get: Term.Name = q"get"
  final val Print: Term.Name = q"print"
  final val Println: Term.Name = q"println"

  final val Scala: Term.Name = q"scala"
  
  final val ScalaRange: Term.Name = q"Range"
  final val ScalaInclusive: Term.Name = q"inclusive"
  final val ScalaOption: Term.Name = q"Option"
  final val ScalaSome: Term.Name = q"Some"
  final val ScalaNone: Term.Name = q"None"
  final val ScalaRight: Term.Name = q"Right"
  final val ScalaLeft: Term.Name = q"Left"
  final val ScalaSuccess: Term.Name = q"Success"
  final val ScalaFailure: Term.Name = q"Failure"
  final val ScalaSuccessful: Term.Name = q"successful"
  final val ScalaFailed: Term.Name = q"failed"
  final val ScalaVector: Term.Name = q"Vector"
  final val ScalaNil: Term.Name = q"Nil"
  final val ScalaTo: Term.Name = q"to"
  final val ScalaUntil: Term.Name = q"until"
  final val ScalaAssociation: Term.Name = Term.Name("->")
  final val ScalaClassOf: Term.Name = q"classOf"

  final val Java: Term.Name = q"java"
  final val JavaIntStream: Term.Name = q"IntStream"
  final val JavaRange: Term.Name = q"range"
  final val JavaRangeClosed: Term.Name = q"rangeClosed"
  final val JavaCompletableFuture: Term.Name = q"CompletableFuture"
  final val JavaSupplyAsync: Term.Name = q"supplyAsync"
  final val JavaCompletedFuture: Term.Name = q"completedFuture"
  final val JavaFailedFuture: Term.Name = q"failedFuture"
  final val JavaOptional: Term.Name = q"Optional"
  final val JavaOf: Term.Name = q"of"
  final val JavaOfNullable: Term.Name = q"ofNullable"
  final val JavaOfEntries: Term.Name = q"ofEntries"
  final val JavaOfSupplier: Term.Name = q"ofSupplier"
  final val JavaSuccess: Term.Name = q"success"
  final val JavaFailure: Term.Name = q"failure"

  final val JavaRun: Term.Name = q"run"
  final val JavaAccept: Term.Name = q"accept"
}
