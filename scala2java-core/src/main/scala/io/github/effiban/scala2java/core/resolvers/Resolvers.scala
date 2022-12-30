package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.classifiers.Classifiers

class Resolvers(implicit classifiers: Classifiers) {
  import classifiers._

  lazy val shouldReturnValueResolver: ShouldReturnValueResolver = new ShouldReturnValueResolverImpl(termTypeClassifier)
}
