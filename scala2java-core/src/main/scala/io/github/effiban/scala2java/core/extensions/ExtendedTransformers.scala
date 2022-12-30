package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.transformers._

private[extensions] trait ExtendedTransformers { this: ExtensionContainer =>

  val fileNameTransformers: List[FileNameTransformer] = extensions.map(_.fileNameTransformer())

  val importerTransformers: List[ImporterTransformer] = extensions.map(_.importerTransformer())

  val classTransformers: List[ClassTransformer] = extensions.map(_.classTransformer())

  val defnValTransformers: List[DefnValTransformer] = extensions.map(_.defnValTransformer())

  val defnValToDeclVarTransformers: List[DefnValToDeclVarTransformer] = extensions.map(_.defnValToDeclVarTransformer())

  val defnDefTransformers: List[DefnDefTransformer] = extensions.map(_.defnDefTransformer())

  val termApplyTypeToTermApplyTransformers: List[TermApplyTypeToTermApplyTransformer] =
    extensions.map(_.termApplyTypeToTermApplyTransformer())

  val termApplyTransformers: List[TermApplyTransformer] = extensions.map(_.termApplyTransformer())

  val termSelectTransformers: List[TermSelectTransformer] = extensions.map(_.termSelectTransformer())

  val typeNameTransformers: List[TypeNameTransformer] = extensions.map(_.typeNameTransformer())

}
