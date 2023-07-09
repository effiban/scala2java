package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.transformers._

private[extensions] trait ExtendedTransformers { this: ExtensionContainer =>

  val fileNameTransformers: List[FileNameTransformer] = extensions.map(_.fileNameTransformer())

  val importerTransformers: List[ImporterTransformer] = extensions.map(_.importerTransformer())

  val classTransformers: List[ClassTransformer] = extensions.map(_.classTransformer())

  val templateTermApplyInfixToDefnTransformers: List[TemplateTermApplyInfixToDefnTransformer] =
    extensions.map(_.templateTermApplyInfixToDefnTransformer())

  val templateTermApplyToDefnTransformers: List[TemplateTermApplyToDefnTransformer] = extensions.map(_.templateTermApplyToDefnTransformer())

  val defnVarTransformers: List[DefnVarTransformer] = extensions.map(_.defnVarTransformer())

  val defnVarToDeclVarTransformers: List[DefnVarToDeclVarTransformer] = extensions.map(_.defnVarToDeclVarTransformer())

  val defnDefTransformers: List[DefnDefTransformer] = extensions.map(_.defnDefTransformer())

  val termApplyInfixToTermApplyTransformers: List[TermApplyInfixToTermApplyTransformer] = extensions.map(_.termApplyInfixToTermApplyTransformer())

  val termApplyTransformers: List[TermApplyTransformer] = extensions.map(_.termApplyTransformer())

  val termSelectTransformers: List[TermSelectTransformer] = extensions.map(_.termSelectTransformer())

  val termNameTransformers: List[TermNameTransformer] = extensions.map(_.termNameTransformer())

  val typeNameTransformers: List[TypeNameTransformer] = extensions.map(_.typeNameTransformer())

}
