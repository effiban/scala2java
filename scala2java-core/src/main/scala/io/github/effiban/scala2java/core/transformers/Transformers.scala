package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.{CompositeTypeClassifier, TermNameClassifier, TermSelectClassifier}
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.typeinference.TypeInferrers
import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer

class Transformers(typeInferrers: => TypeInferrers)
                  (implicit extensionRegistry: ExtensionRegistry) {

  private lazy val coreTermApplyTransformer: TermApplyTransformer = new CoreTermApplyTransformer(
    TermSelectClassifier,
    CompositeTypeClassifier,
    termSelectTermFunctionTransformer,
    deprecatedCoreTermApplyTransformer
  )

  private lazy val deprecatedCoreTermApplyTransformer: DeprecatedCoreTermApplyTransformer = new DeprecatedCoreTermApplyTransformer(
    TermNameClassifier,
    CompositeTypeClassifier,
    termSelectTermFunctionTransformer,
  )

  lazy val internalTermApplyTransformer: InternalTermApplyTransformer = new InternalTermApplyTransformerImpl(
    new CompositeTermApplyTransformer(coreTermApplyTransformer)
  )

  private lazy val termSelectTermFunctionTransformer: TermSelectTermFunctionTransformer = new TermSelectTermFunctionTransformerImpl(
    typeInferrers.functionTypeInferrer,
    FunctionTypeTransformer
  )
}
