package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.typeinference.QualifierTypeInferrer
import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.transformers.{TermSelectNameTransformer, TermSelectTransformer}

import scala.meta.Term

trait InternalTermSelectTransformer {
  def transform(termSelect: Term.Select): Term
}

private[transformers] class InternalTermSelectTransformerImpl(treeTransformer: => TreeTransformer,
                                                              termSelectTransformer: TermSelectTransformer,
                                                              termSelectNameTransformer: TermSelectNameTransformer,
                                                              qualifierTypeInferrer: => QualifierTypeInferrer) extends InternalTermSelectTransformer {

  override def transform(termSelect: Term.Select): Term = {
    termSelectTransformer.transform(termSelect) match {
      case Some(transformedSelect) => transformedSelect
      case None =>
        val maybeQualType = qualifierTypeInferrer.infer(termSelect)
        val transformedName = termSelectNameTransformer.transform(termSelect.name, TermSelectTransformationContext(maybeQualType))
        val transformedQualifier = treeTransformer.transform(termSelect.qual).asInstanceOf[Term]
        Term.Select(transformedQualifier, transformedName)
    }
  }
}
