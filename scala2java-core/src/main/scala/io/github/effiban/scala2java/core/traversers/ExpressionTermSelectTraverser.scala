package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.typeinference.QualifierTypeInferrer
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.{TermSelectNameTransformer, TermSelectTransformer}

import scala.meta.Term

trait ExpressionTermSelectTraverser extends ScalaTreeTraverser2[Term.Select, Term]

private[traversers] class ExpressionTermSelectTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                            qualifierTypeInferrer: => QualifierTypeInferrer,
                                                            termSelectTransformer: TermSelectTransformer,
                                                            termSelectNameTransformer: TermSelectNameTransformer)
  extends ExpressionTermSelectTraverser {

  // qualified name in the context of an evaluated expression, that might need to be transformed into a Java equivalent
  override def traverse(select: Term.Select): Term = {
    val transformedTerm = termSelectTransformer.transform(select) match {
      case Some(transformedSelect) => transformedSelect
      case None =>
        val maybeQualType = qualifierTypeInferrer.infer(select)
        val transformedName = termSelectNameTransformer.transform(select.name, TermSelectTransformationContext(maybeQualType))
        select.copy(name = transformedName)
    }

    transformedTerm match {
      case transformedSelect: Term.Select => traverseAsSelect(transformedSelect)
      case term => expressionTermTraverser.traverse(term)
    }
  }

  private def traverseAsSelect(transformedSelect: Term.Select): Term.Select = {
    val traversedQual = expressionTermTraverser.traverse(transformedSelect.qual)
    transformedSelect.copy(qual = traversedQual)
  }
}
