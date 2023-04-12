package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TermNameValues.ScalaTupleElementRegex
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.typeinferrers.SelectTypeInferrer

import scala.meta.{Term, Type}


object CoreSelectTypeInferrer extends SelectTypeInferrer {

  override def infer(termSelect: Term.Select, context: TermSelectInferenceContext): Option[Type] = {

    (termSelect.qual, context.maybeQualType, termSelect.name) match {
      case (_, Some(Type.Tuple(typeArgs)), Term.Name(ScalaTupleElementRegex(indexStr))) => Some(typeArgs.apply(indexStr.toInt - 1))
     // TODO - add more inferrable qualified names that are not method invocations
      case _ => None
    }
  }
}