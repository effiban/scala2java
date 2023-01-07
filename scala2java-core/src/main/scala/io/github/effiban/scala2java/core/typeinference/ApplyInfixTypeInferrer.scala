package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.TermApplyInfixClassifier
import io.github.effiban.scala2java.core.entities.TermApplyInfixKind.Association
import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer0

import scala.meta.{Term, Type}

trait ApplyInfixTypeInferrer extends TypeInferrer0[Term.ApplyInfix]

private[typeinference] class ApplyInfixTypeInferrerImpl(tupleTypeInferrer: => TupleTypeInferrer,
                                                        termApplyInfixClassifier: TermApplyInfixClassifier) extends ApplyInfixTypeInferrer {

  override def infer(termApplyInfix: Term.ApplyInfix): Option[Type] = {
    termApplyInfix match {
      case termApplyInfix: Term.ApplyInfix if termApplyInfixClassifier.classify(termApplyInfix) == Association =>
        Some(tupleTypeInferrer.infer(Term.Tuple(termApplyInfix.lhs +: termApplyInfix.args)))
        // TODO - handle more cases
      case _ => None
    }
  }
}