package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.{CompositeTypeClassifier, TermNameClassifier}
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.predicates.Predicates
import io.github.effiban.scala2java.core.typeinference.TypeInferrers
import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer

class Transformers(typeInferrers: => TypeInferrers,
                   predicates: => Predicates)
                  (implicit extensionRegistry: ExtensionRegistry) {

  private lazy val coreTermApplyTransformer: TermApplyTransformer = new CoreTermApplyTransformer(
    TermNameClassifier,
    CompositeTypeClassifier,
    termSelectTermFunctionTransformer
  )

  lazy val defaultInternalTermNameTransformer: InternalTermNameTransformer = new DefaultInternalTermNameTransformer(
    new CompositeTermNameTransformer(CoreTermNameTransformer)
  )

  lazy val defaultInternalTermSelectTransformer: InternalTermSelectTransformer = new DefaultInternalTermSelectTransformer(
    new CompositeTermSelectTransformer(CoreTermSelectTransformer)
  )

  lazy val evaluatedInternalTermNameTransformer: EvaluatedInternalTermNameTransformer = new EvaluatedInternalTermNameTransformer(
    defaultInternalTermNameTransformer,
    predicates.compositeTermNameSupportsNoArgInvocation
  )

  lazy val evaluatedInternalTermSelectTransformer: EvaluatedInternalTermSelectTransformer = new EvaluatedInternalTermSelectTransformer(
    defaultInternalTermSelectTransformer,
    predicates.compositeTermSelectSupportsNoArgInvocation
  )

  lazy val internalTermApplyTransformer: InternalTermApplyTransformer = new InternalTermApplyTransformerImpl(
    new CompositeTermApplyTransformer(coreTermApplyTransformer),
    TermNameClassifier
  )

  private lazy val termSelectTermFunctionTransformer: TermSelectTermFunctionTransformer = new TermSelectTermFunctionTransformerImpl(
    typeInferrers.functionTypeInferrer,
    FunctionTypeTransformer
  )
}
