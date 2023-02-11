package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.typeinference.TypeInferrers
import io.github.effiban.scala2java.spi.transformers.TermSelectTransformer

class Transformers(typeInferrers: => TypeInferrers) {

  lazy val coreTermSelectTransformer: TermSelectTransformer = new CoreTermSelectTransformer(
    TermNameClassifier,
    termSelectTermFunctionTransformer
  )

  private lazy val termSelectTermFunctionTransformer: TermSelectTermFunctionTransformer = new TermSelectTermFunctionTransformerImpl(
    typeInferrers.functionTypeInferrer,
    FunctionTypeTransformer
  )
}
