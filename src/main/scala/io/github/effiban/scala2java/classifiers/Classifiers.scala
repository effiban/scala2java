package io.github.effiban.scala2java.classifiers

import io.github.effiban.scala2java.typeinference.TypeInferrers.termTypeInferrer

object Classifiers {

  lazy val termTypeClassifier: TermTypeClassifier = new TermTypeClassifierImpl(
    termTypeInferrer,
    TermApplyInfixClassifier)
}
