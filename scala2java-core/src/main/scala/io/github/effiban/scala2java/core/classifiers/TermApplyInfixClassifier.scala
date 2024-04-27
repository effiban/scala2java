package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.entities.TermApplyInfixKind.{Association, Operator, Range, TermApplyInfixKind, Unclassified}

import scala.meta.Term

trait TermApplyInfixClassifier {

  def classify(termApplyInfix: Term.ApplyInfix): TermApplyInfixKind
}

object TermApplyInfixClassifier extends TermApplyInfixClassifier {

  private final val ScalaRangeOperators = List("to", "until")
  private final val JavaStyleOperators = List(
    "+",
    "-",
    "*",
    "/",
    "%",
    "&&",
    "||",
    "&",
    "|",
    "^",
    "==",
    "!=",
    ">",
    ">=",
    "<",
    "<="
  )

  override def classify(termApplyInfix: Term.ApplyInfix): TermApplyInfixKind = termApplyInfix.op.value match {
    case "->" => Association
    case operator if ScalaRangeOperators.contains(operator) => Range
    case operator if JavaStyleOperators.contains(operator) => Operator
    case _ => Unclassified
  }
}
