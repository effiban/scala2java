package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.typeinference.TypeInferrers
import io.github.effiban.scala2java.spi.transformers.{TermNameTransformer, TermSelectTransformer}

class Transformers(typeInferrers: => TypeInferrers)
                  (implicit extensionRegistry: ExtensionRegistry) {

  private lazy val compositeTermNameTransformer: TermNameTransformer =
    new CompositeTermNameTransformer(CoreTermNameTransformer)

  lazy val coreTermSelectTransformer: TermSelectTransformer = new CoreTermSelectTransformer(
    TermNameClassifier,
    termSelectTermFunctionTransformer
  )

  lazy val internalTermNameTransformer: InternalTermNameTransformer = new InternalTermNameTransformerImpl(
    compositeTermNameTransformer
  )

  private lazy val termSelectTermFunctionTransformer: TermSelectTermFunctionTransformer = new TermSelectTermFunctionTransformerImpl(
    typeInferrers.functionTypeInferrer,
    FunctionTypeTransformer
  )
}
