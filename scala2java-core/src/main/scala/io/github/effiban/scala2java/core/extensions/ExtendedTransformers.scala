package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.transformers._

private[extensions] trait ExtendedTransformers { this: ExtensionContainer =>

  val classTransformers: List[ClassTransformer] = extensions.map(_.classTransformer())

  val templateTermApplyInfixToDefnTransformers: List[TemplateTermApplyInfixToDefnTransformer] =
    extensions.map(_.templateTermApplyInfixToDefnTransformer())

  val templateTermApplyToDefnTransformers: List[TemplateTermApplyToDefnTransformer] = extensions.map(_.templateTermApplyToDefnTransformer())

  val defnVarTransformers: List[DefnVarTransformer] = extensions.map(_.defnVarTransformer())

  val defnVarToDeclVarTransformers: List[DefnVarToDeclVarTransformer] = extensions.map(_.defnVarToDeclVarTransformer())

  val defnDefTransformers: List[DefnDefTransformer] = extensions.map(_.defnDefTransformer())

  val termApplyInfixToTermApplyTransformers: List[TermApplyInfixToTermApplyTransformer] = extensions.map(_.termApplyInfixToTermApplyTransformer())

  val qualifiedTermApplyTransformers: List[QualifiedTermApplyTransformer] = extensions.map(_.qualifiedTermApplyTransformer())

  val unqualifiedTermApplyTransformers: List[UnqualifiedTermApplyTransformer] = extensions.map(_.unqualifiedTermApplyTransformer())

  val termSelectTransformers: List[TermSelectTransformer] = extensions.map(_.termSelectTransformer())

  val termSelectNameTransformers: List[TermSelectNameTransformer] = extensions.map(_.termSelectNameTransformer())

  val typeSelectTransformers: List[TypeSelectTransformer] = extensions.map(_.typeSelectTransformer())
}
