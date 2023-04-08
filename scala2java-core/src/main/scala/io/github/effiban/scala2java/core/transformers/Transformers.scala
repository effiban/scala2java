package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.predicates.Predicates
import io.github.effiban.scala2java.core.typeinference.TypeInferrers
import io.github.effiban.scala2java.spi.transformers.{TermNameTransformer, TermSelectTransformer}

class Transformers(typeInferrers: => TypeInferrers,
                   predicates: => Predicates)
                  (implicit extensionRegistry: ExtensionRegistry) {

  private lazy val compositeTermNameTransformer: TermNameTransformer =
    new CompositeTermNameTransformer(CoreTermNameTransformer)

  lazy val coreTermSelectTransformer: TermSelectTransformer = new CoreTermSelectTransformer(
    TermNameClassifier,
    termSelectTermFunctionTransformer
  )

  lazy val defaultInternalTermNameTransformer: InternalTermNameTransformer = new DefaultInternalTermNameTransformer(
    compositeTermNameTransformer
  )

  lazy val evaluatedInternalTermNameTransformer: EvaluatedInternalTermNameTransformer = new EvaluatedInternalTermNameTransformer(
    defaultInternalTermNameTransformer,
    predicates.compositeTermNameSupportsNoArgInvocation
  )

  private lazy val termSelectTermFunctionTransformer: TermSelectTermFunctionTransformer = new TermSelectTermFunctionTransformerImpl(
    typeInferrers.functionTypeInferrer,
    FunctionTypeTransformer
  )
}
