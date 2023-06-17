package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.typeinference.QualifierTypeInferrer
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer

import scala.meta.Term

trait ExpressionTermSelectTraverser extends ScalaTreeTraverser2[Term.Select, Term]

private[traversers] class ExpressionTermSelectTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                            qualifierTypeInferrer: => QualifierTypeInferrer,
                                                            termSelectTransformer: TermSelectTransformer)
  extends ExpressionTermSelectTraverser {

  // qualified name in the context of an evaluated expression, that might need to be transformed into a Java equivalent
  override def traverse(select: Term.Select): Term = {
    val maybeQualType = qualifierTypeInferrer.infer(select)
    val transformedTerm = termSelectTransformer.transform(select, TermSelectTransformationContext(maybeQualType))
    transformedTerm match {
      case Some(transformedSelect: Term.Select) => traverseAsSelect(transformedSelect)
      case Some(term) => expressionTermTraverser.traverse(term)
      case None => traverseAsSelect(select)
    }
  }

  private def traverseAsSelect(transformedSelect: Term.Select): Term.Select = {
    val traversedQual = expressionTermTraverser.traverse(transformedSelect.qual)
    transformedSelect.copy(qual = traversedQual)
  }
}
