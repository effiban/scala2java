package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.{CompositeTypeClassifier, TermSelectClassifier}
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.typeinference.TypeInferrers
import io.github.effiban.scala2java.spi.transformers.{QualifiedTermApplyTransformer, TypeSelectTransformer, UnqualifiedTermApplyTransformer}

class Transformers(implicit typeInferrers: => TypeInferrers,
                   extensionRegistry: ExtensionRegistry) {

  private lazy val coreQualifiedTermApplyTransformer: QualifiedTermApplyTransformer = new CoreQualifiedTermApplyTransformer(
    TermSelectClassifier
  )

  private lazy val coreUnqualifiedTermApplyTransformer: UnqualifiedTermApplyTransformer = new CoreUnqualifiedTermApplyTransformer(
    CompositeTypeClassifier
  )

  lazy val internalTermApplyTransformer: InternalTermApplyTransformer = new InternalTermApplyTransformerImpl(
    treeTransformer,
    new CompositeQualifiedTermApplyTransformer(coreQualifiedTermApplyTransformer),
    new CompositeUnqualifiedTermApplyTransformer(coreUnqualifiedTermApplyTransformer),
    termSelectTermFunctionTransformer
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
