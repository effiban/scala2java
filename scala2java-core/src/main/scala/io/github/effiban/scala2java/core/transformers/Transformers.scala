package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.{CompositeTypeClassifier, TermSelectClassifier}
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.factories.Factories
import io.github.effiban.scala2java.core.typeinference.{InnermostEnclosingTemplateAncestorsInferrer, InnermostEnclosingTemplateInferrer, TypeInferrers}
import io.github.effiban.scala2java.spi.transformers.{QualifiedTermApplyTransformer, TypeSelectTransformer, UnqualifiedTermApplyTransformer}

class Transformers(implicit typeInferrers: => TypeInferrers,
                   factories: => Factories,
                   extensionRegistry: ExtensionRegistry) {

  private lazy val coreQualifiedTermApplyTransformer: QualifiedTermApplyTransformer = new CoreQualifiedTermApplyTransformer(
    TermSelectClassifier
  )

  private lazy val coreUnqualifiedTermApplyTransformer: UnqualifiedTermApplyTransformer = new CoreUnqualifiedTermApplyTransformer(
    CompositeTypeClassifier
  )

  lazy val functionTypeTransformer: FunctionTypeTransformer = new FunctionTypeTransformerImpl(treeTransformer)

  lazy val internalTermApplyTransformer: InternalTermApplyTransformer = new InternalTermApplyTransformerImpl(
    treeTransformer,
    new CompositeQualifiedTermApplyTransformer(coreQualifiedTermApplyTransformer),
    new CompositeUnqualifiedTermApplyTransformer(coreUnqualifiedTermApplyTransformer),
    termSelectTermFunctionTransformer,
    factories.termApplyTransformationContextFactory
  )

  private lazy val internalTermApplyInfixTransformer: InternalTermApplyInfixTransformer = new InternalTermApplyInfixTransformerImpl(
    new CompositeTermApplyInfixToTermApplyTransformer(CoreTermApplyInfixToTermApplyTransformer),
    treeTransformer
  )

  private lazy val internalTermSelectTransformer: InternalTermSelectTransformer = new InternalTermSelectTransformerImpl(
    treeTransformer,
    new CompositeTermSelectTransformer(CoreTermSelectTransformer),
    new CompositeTermSelectNameTransformer(CoreTermSelectNameTransformer),
    typeInferrers.qualifierTypeInferrer
  )

  private lazy val pkgTransformer: PkgTransformer = new PkgTransformerImpl(treeTransformer)

  val sourceTransformer: SourceTransformer = new SourceTransformerImpl(treeTransformer)

  private lazy val templateTransformer: TemplateTransformer = new TemplateTransformerImpl(treeTransformer)

  private lazy val termSelectTermFunctionTransformer: TermSelectTermFunctionTransformer = new TermSelectTermFunctionTransformerImpl(
    typeInferrers.functionTypeInferrer,
    treeTransformer
  )

  private lazy val termSuperTransformer: TermSuperTransformer = new TermSuperTransformerImpl(
    InnermostEnclosingTemplateInferrer,
    treeTransformer
  )

  private lazy val termTupleToTermApplyTransformer: TermTupleToTermApplyTransformer = new TermTupleToTermApplyTransformerImpl(treeTransformer)

  private lazy val treeTransformer: TreeTransformer = new TreeTransformerImpl(
    pkgTransformer,
    templateTransformer,
    internalTermApplyInfixTransformer,
    internalTermApplyTransformer,
    internalTermSelectTransformer,
    termTupleToTermApplyTransformer,
    termSuperTransformer,
    functionTypeTransformer,
    typeSelectTransformer,
    typeTupleToTermApplyTransformer
  )

  private val typeSelectTransformer: TypeSelectTransformer = new CompositeTypeSelectTransformer(CoreTypeSelectTransformer)

  private lazy val typeTupleToTermApplyTransformer: TypeTupleToTypeApplyTransformer = new TypeTupleToTypeApplyTransformerImpl(treeTransformer)
}
