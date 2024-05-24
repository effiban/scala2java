package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TypeClassifier
import io.github.effiban.scala2java.spi.contexts.UnqualifiedTermApplyTransformationContext
import io.github.effiban.scala2java.spi.entities.UnqualifiedTermApply
import io.github.effiban.scala2java.spi.transformers.UnqualifiedTermApplyTransformer

import scala.meta.{Type, XtensionQuasiquoteTerm}

private[transformers] class CoreUnqualifiedTermApplyTransformer(typeClassifier: TypeClassifier[Type])
  extends UnqualifiedTermApplyTransformer {

  override final def transform(unqualifiedTermApply: UnqualifiedTermApply,
                               context: UnqualifiedTermApplyTransformationContext = UnqualifiedTermApplyTransformationContext())
    : Option[UnqualifiedTermApply] =

    (context.maybeQualifierType, unqualifiedTermApply.name, unqualifiedTermApply.args) match {
      case (_, q"foreach", _) => Some(unqualifiedTermApply.copy(name = q"forEach"))
      case (Some(parentType), q"take", arg :: Nil) if typeClassifier.isJavaListLike(parentType) =>
        Some(unqualifiedTermApply.copy(name = q"subList", args = List(q"0", arg)))
      case (Some(parentType), q"length", Nil) if typeClassifier.isJavaListLike(parentType) =>
        Some(unqualifiedTermApply.copy(name = q"size"))
      case _ => None
    }
}
