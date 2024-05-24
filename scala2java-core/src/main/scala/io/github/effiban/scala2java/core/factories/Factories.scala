package io.github.effiban.scala2java.core.factories

import io.github.effiban.scala2java.core.typeinference.TypeInferrers

class Factories(typeInferrers: => TypeInferrers) {

  lazy val termApplyInferenceContextFactory: TermApplyInferenceContextFactory = new TermApplyInferenceContextFactoryImpl(
    typeInferrers.applyParentTypeInferrer,
    typeInferrers.termTypeInferrer,
  )

  lazy val unqualifiedTermApplyTransformationContextFactory: UnqualifiedTermApplyTransformationContextFactory = new UnqualifiedTermApplyTransformationContextFactoryImpl(
    termApplyInferenceContextFactory,
    typeInferrers.internalApplyDeclDefInferrer
  )
}
