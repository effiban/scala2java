package effiban.scala2java.resolvers

import effiban.scala2java.classifiers.Classifiers.termTypeClassifier

object Resolvers {

  lazy val shouldReturnValueResolver: ShouldReturnValueResolver = new ShouldReturnValueResolverImpl(termTypeClassifier)
}
