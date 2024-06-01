package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.factories.UnqualifiedTermApplyTransformationContextFactory
import io.github.effiban.scala2java.spi.contexts.UnqualifiedTermApplyTransformationContext
import io.github.effiban.scala2java.spi.entities.QualifiedTermApply
import io.github.effiban.scala2java.spi.transformers.{QualifiedTermApplyTransformer, UnqualifiedTermApplyTransformer}

import scala.meta.{Term, Type}

trait InternalTermApplyTransformer {
  def transform(termApply: Term.Apply): Term.Apply
}

private[transformers] class InternalTermApplyTransformerImpl(
  treeTransformer: => TreeTransformer,
  qualifiedTermApplyTransformer: QualifiedTermApplyTransformer,
  unqualifiedTermApplyTransformer: UnqualifiedTermApplyTransformer,
  termSelectTermFunctionTransformer: TermSelectTermFunctionTransformer,
  unqualifiedTermApplyTransformationContextFactory: => UnqualifiedTermApplyTransformationContextFactory) extends InternalTermApplyTransformer {

  override final def transform(termApply: Term.Apply): Term.Apply = {
    val context = unqualifiedTermApplyTransformationContextFactory.create(termApply)
    val transformedArgs = termApply.args.map(treeTransformer.transform(_).asInstanceOf[Term])

    termApply.fun match {
      case Term.ApplyType(qualifiedName: Term.Select, typeArgs) =>
        transformWhenTypedWithQualifiedName(qualifiedName, typeArgs, transformedArgs, context)

      case Term.Select(termFunction: Term.Function, name) =>
        transformWhenQualifierIsLambda(termFunction, name, transformedArgs)

      case qualifiedName: Term.Select => transformWhenUntypedWithQualifiedName(qualifiedName, transformedArgs, context)

      case _ => transformFun(termApply, transformedArgs)
    }
  }

  private def transformWhenTypedWithQualifiedName(qualifiedName: Term.Select,
                                                  typeArgs: List[Type],
                                                  transformedArgs: List[Term],
                                                  context: UnqualifiedTermApplyTransformationContext) = {
    val transformedTypeArgs = typeArgs.map(treeTransformer.transform(_).asInstanceOf[Type])
    val qualifiedTermApply = QualifiedTermApply(qualifiedName, transformedTypeArgs, transformedArgs)
    transformWhenHasQualifiedName(qualifiedTermApply, context)
  }

  private def transformWhenUntypedWithQualifiedName(qualifiedName: Term.Select,
                                                    transformedArgs: List[Term],
                                                    context: UnqualifiedTermApplyTransformationContext) = {
    val qualifiedTermApply = QualifiedTermApply(qualifiedName, Nil, transformedArgs)
    transformWhenHasQualifiedName(qualifiedTermApply, context)
  }

  private def transformWhenHasQualifiedName(qualifiedTermApply: QualifiedTermApply,
                                            context: UnqualifiedTermApplyTransformationContext) = {
    val transformedQualifiedTermApply = qualifiedTermApplyTransformer.transform(qualifiedTermApply, context.asQualifiedContext())
      .getOrElse(transformWhenHasQualifiedNameInParts(qualifiedTermApply, context))
    transformedQualifiedTermApply.asTermApply()
  }

  private def transformWhenHasQualifiedNameInParts(qualifiedTermApply: QualifiedTermApply, context: UnqualifiedTermApplyTransformationContext) = {
    val unqualifiedTermApply = qualifiedTermApply.asUnqualified()
    val transformedUnqualifiedTermApply = unqualifiedTermApplyTransformer.transform(unqualifiedTermApply, context)
      .getOrElse(unqualifiedTermApply)

    val transformedQual = treeTransformer.transform(qualifiedTermApply.qualifiedName.qual).asInstanceOf[Term]
    transformedUnqualifiedTermApply.qualifiedBy(transformedQual)
  }

  private def transformWhenQualifierIsLambda(termFunction: Term.Function,
                                             name: Term.Name,
                                             transformedArgs: List[Term]) = {
    val transformedTermFunction = termSelectTermFunctionTransformer.transform(termFunction, name)
    Term.Apply(transformedTermFunction, transformedArgs)
  }

  private def transformFun(termApply: Term.Apply, transformedArgs: List[Term]) = {
    val transformedFun = treeTransformer.transform(termApply.fun).asInstanceOf[Term]
    Term.Apply(transformedFun, transformedArgs)
  }
}
