package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.classifiers.Classifiers.termTypeClassifier

object Resolvers {

  lazy val shouldReturnValueResolver: ShouldReturnValueResolver = new ShouldReturnValueResolverImpl(termTypeClassifier)
}
