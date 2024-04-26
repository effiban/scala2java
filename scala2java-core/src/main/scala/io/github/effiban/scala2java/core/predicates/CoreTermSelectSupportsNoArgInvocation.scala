package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.{CompositeTypeClassifier, TermSelectClassifier, TypeClassifier}
import io.github.effiban.scala2java.core.entities.TermNameValues.{Apply, Empty}
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation

import scala.meta.{Term, Type, XtensionQuasiquoteTerm}

private[predicates] class CoreTermSelectSupportsNoArgInvocation(termSelectClassifier: TermSelectClassifier,
                                                                typeClassifier: TypeClassifier[Type])
  extends TermSelectSupportsNoArgInvocation {

  override def apply(termSelect: Term.Select, context: TermSelectInferenceContext = TermSelectInferenceContext()): Boolean = {
    supportsByQualifiedName(termSelect) || supportsByQualifierTypeAndName(context.maybeQualType, termSelect.name)
  }

  private def supportsByQualifiedName(termSelect: Term.Select) = termSelect match {
      case aTermSelect if termSelectClassifier.supportsNoArgInvocation(aTermSelect) => true
      case Term.Select(qual: Term.Select, Term.Name(Apply)) if termSelectClassifier.hasApplyMethod(qual) => true
      case Term.Select(qual: Term.Select, Term.Name(Empty)) if termSelectClassifier.hasEmptyMethod(qual) => true
      case Term.Select(_, q"toString") => true
      case _ => false
    }

  private def supportsByQualifierTypeAndName(maybeQualType: Option[Type], name: Term.Name): Boolean = {
    (maybeQualType, name) match {
      case (Some(qualType), q"length") if typeClassifier.isJavaListLike(qualType) => true
      case _ => false
    }
  }
}

object CoreTermSelectSupportsNoArgInvocation extends CoreTermSelectSupportsNoArgInvocation(
  TermSelectClassifier,
  CompositeTypeClassifier
)
