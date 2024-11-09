package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.extractors.TreeNameExtractor
import io.github.effiban.scala2java.core.typeinference.InnermostEnclosingTemplateInferrer

import scala.meta.{Name, Template, Term}

trait TermSuperTransformer {
  def transform(termSuper: Term.Super): Term.Super
}

private[transformers] class TermSuperTransformerImpl(innermostEnclosingTemplateInferrer: InnermostEnclosingTemplateInferrer,
                                                     treeTransformer: => TreeTransformer) extends TermSuperTransformer {

  override def transform(termSuper: Term.Super): Term.Super = {
    val transformedSuperp = termSuper.superp match {
      case name: Name.Indeterminate => transformSuperName(termSuper, name)
      case superp => superp
    }
    termSuper.copy(superp = transformedSuperp)
  }

  private def transformSuperName(termSuper: Term.Super, originalName: Name) = {
    innermostEnclosingTemplateInferrer.infer(termSuper)
      .toList
      .flatMap(directParentTypesOf)
      .find(TreeNameExtractor.extract(_).value == originalName.value)
      .map(treeTransformer.transform)
      .map(TreeNameExtractor.extract)
      .getOrElse(originalName)
  }

  private def directParentTypesOf(template: Template) = {
    template.inits.map(_.tpe) ++ template.self.decltpe
  }
}
