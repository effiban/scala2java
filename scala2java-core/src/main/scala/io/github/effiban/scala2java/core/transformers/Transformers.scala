package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.predicates.Predicates
import io.github.effiban.scala2java.core.typeinference.TypeInferrers
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer

class Transformers(typeInferrers: => TypeInferrers,
                   predicates: => Predicates)
                  (implicit extensionRegistry: ExtensionRegistry) {

  private lazy val coreTermSelectTransformer: TermSelectTransformer = new CoreTermSelectTransformer(
    TermNameClassifier,
    termSelectTermFunctionTransformer
  )

  lazy val defaultInternalTermNameTransformer: InternalTermNameTransformer = new DefaultInternalTermNameTransformer(
    new CompositeTermNameTransformer(CoreTermNameTransformer)
  )

  lazy val defaultInternalTermSelectTransformer: InternalTermSelectTransformer = new DefaultInternalTermSelectTransformer(
    new CompositeTermSelectTransformer(coreTermSelectTransformer)
  )

  lazy val evaluatedInternalTermNameTransformer: EvaluatedInternalTermNameTransformer = new EvaluatedInternalTermNameTransformer(
    defaultInternalTermNameTransformer,
    predicates.compositeTermNameSupportsNoArgInvocation
  )

  lazy val internalTermApplyTransformer: InternalTermApplyTransformer = new InternalTermApplyTransformerImpl(
    new CompositeTermApplyTransformer(CoreTermApplyTransformer),
    TermNameClassifier
  )

  private lazy val termSelectTermFunctionTransformer: TermSelectTermFunctionTransformer = new TermSelectTermFunctionTransformerImpl(
    typeInferrers.functionTypeInferrer,
    FunctionTypeTransformer
  )
}
