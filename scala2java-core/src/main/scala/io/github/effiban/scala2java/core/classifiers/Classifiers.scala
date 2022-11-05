package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.typeinference.TypeInferrers.termTypeInferrer

object Classifiers {

  lazy val termTypeClassifier: TermTypeClassifier = new TermTypeClassifierImpl(
    termTypeInferrer,
    TermApplyInfixClassifier)
}
