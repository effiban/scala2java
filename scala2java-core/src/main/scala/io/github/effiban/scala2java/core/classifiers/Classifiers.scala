package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.typeinference.TypeInferrers

class Classifiers(typeInferrers: => TypeInferrers) {

  lazy val termTypeClassifier: TermTypeClassifier = new TermTypeClassifierImpl(
    typeInferrers.termTypeInferrer,
    TermApplyInfixClassifier)
}
