package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.{CompositeTypeClassifier, TermSelectClassifier}
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.typeinference.TypeInferrers
import io.github.effiban.scala2java.spi.transformers.{TermApplyTransformer, TypeSelectTransformer}

class Transformers(implicit typeInferrers: => TypeInferrers,
                   extensionRegistry: ExtensionRegistry) {

  private lazy val coreTermApplyTransformer: TermApplyTransformer = new CoreTermApplyTransformer(
    TermSelectClassifier,
    CompositeTypeClassifier,
    termSelectTermFunctionTransformer
  )

  lazy val internalTermApplyTransformer: InternalTermApplyTransformer = new InternalTermApplyTransformerImpl(
    new CompositeTermApplyTransformer(coreTermApplyTransformer)
  )

  private lazy val internalTermSelectTransformer: InternalTermSelectTransformer = new InternalTermSelectTransformerImpl(
    treeTransformer,
    new CompositeTermSelectTransformer(CoreTermSelectTransformer),
    new CompositeTermSelectNameTransformer(CoreTermSelectNameTransformer),
    typeInferrers.qualifierTypeInferrer
  )

  private lazy val pkgTransformer: PkgTransformer = new PkgTransformerImpl(treeTransformer)

  val sourceTransformer: SourceTransformer = new SourceTransformerImpl(treeTransformer)

  private lazy val termSelectTermFunctionTransformer: TermSelectTermFunctionTransformer = new TermSelectTermFunctionTransformerImpl(
    typeInferrers.functionTypeInferrer,
    FunctionTypeTransformer
  )

  private lazy val treeTransformer: TreeTransformer = new TreeTransformerImpl(
    pkgTransformer,
    internalTermSelectTransformer,
    typeSelectTransformer
  )

  private val typeSelectTransformer: TypeSelectTransformer = new CompositeTypeSelectTransformer(CoreTypeSelectTransformer)
}
