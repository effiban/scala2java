package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.{CompositeTypeClassifier, TermNameClassifier, TypeClassifier}
import io.github.effiban.scala2java.core.entities.TermNameValues.{Apply, Empty}
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation

import scala.meta.{Term, Type, XtensionQuasiquoteTerm}

private[predicates] class CoreTermSelectSupportsNoArgInvocation(termNameClassifier: TermNameClassifier,
                                                                typeClassifier: TypeClassifier[Type])
  extends TermSelectSupportsNoArgInvocation {

  override def apply(termSelect: Term.Select, context: TermSelectInferenceContext = TermSelectInferenceContext()): Boolean = {
    supportsByQualifiedName(termSelect) || supportsByQualifierTypeAndName(context.maybeQualType, termSelect.name)
  }

  private def supportsByQualifiedName(termSelect: Term.Select) = {
    (termSelect.qual, termSelect.name) match {
      case (termName: Term.Name, Term.Name(Apply)) if termNameClassifier.hasApplyMethod(termName) => true
      case (termName: Term.Name, Term.Name(Empty)) if termNameClassifier.hasEmptyMethod(termName) => true
      case (_, q"toString") => true
      case _ => false
    }
  }

  private def supportsByQualifierTypeAndName(maybeQualType: Option[Type], name: Term.Name): Boolean = {
    (maybeQualType, name) match {
      case (Some(qualType), q"length") if typeClassifier.isJavaListLike(qualType) => true
      case _ => false
    }
  }
}

object CoreTermSelectSupportsNoArgInvocation extends CoreTermSelectSupportsNoArgInvocation(TermNameClassifier, CompositeTypeClassifier)
