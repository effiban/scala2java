package effiban.scala2java.classifiers

import effiban.scala2java.typeinference.TypeInferrers.termTypeInferrer

object Classifiers {

  lazy val termTypeClassifier: TermTypeClassifier = new TermTypeClassifierImpl(termTypeInferrer)
}
