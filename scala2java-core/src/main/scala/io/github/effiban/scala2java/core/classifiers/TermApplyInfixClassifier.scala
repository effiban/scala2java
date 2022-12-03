package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.entities.TermApplyInfixKind.{Association, Operator, Range, TermApplyInfixKind, Unclassified}
import io.github.effiban.scala2java.core.entities.TermNameValues.{And, BitwiseAnd, BitwiseOr, BitwiseXor, Divide, Equals, GreaterEquals, GreaterThan, LessEquals, LessThan, Minus, Modulus, Multiply, NotEquals, Or, Plus, ScalaAssociate, ScalaTo, ScalaUntil}

import scala.meta.Term

trait TermApplyInfixClassifier {

  def classify(termApplyInfix: Term.ApplyInfix): TermApplyInfixKind
}

object TermApplyInfixClassifier extends TermApplyInfixClassifier {

  private final val ScalaRangeOperators = List(ScalaTo, ScalaUntil)
  private final val JavaStyleOperators = List(
    Plus,
    Minus,
    Multiply,
    Divide,
    Modulus,
    And,
    Or,
    BitwiseAnd,
    BitwiseOr,
    BitwiseXor,
    Equals,
    NotEquals,
    GreaterThan,
    GreaterEquals,
    LessThan,
    LessEquals
  )

  override def classify(termApplyInfix: Term.ApplyInfix): TermApplyInfixKind = termApplyInfix.op.value match {
    case ScalaAssociate => Association
    case operator if ScalaRangeOperators.contains(operator) => Range
    case operator if JavaStyleOperators.contains(operator) => Operator
    case _ => Unclassified
  }
}
