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
      case Name.Anonymous() => Name.Anonymous()
      case name: Name => transformSuperName(termSuper, name)
    }
    termSuper.copy(superp = transformedSuperp)
  }

  private def transformSuperName(termSuper: Term.Super, originalName: Name) = {
    val maybeEnclosingMemberName = termSuper.thisp match {
      case Name.Anonymous() => None
      case thisp => Some(thisp.value)
    }

    innermostEnclosingTemplateInferrer.infer(termSuper, maybeEnclosingMemberName)
      .toList
      .flatMap(directParentTypesOf)
      .find(TreeNameExtractor.extract(_).value == originalName.value)
      .map(treeTransformer.transform)
      .map(TreeNameExtractor.extractIndeterminate)
      .getOrElse(originalName)
  }

  private def directParentTypesOf(template: Template) = {
    template.inits.map(_.tpe) ++ template.self.decltpe
  }
}
