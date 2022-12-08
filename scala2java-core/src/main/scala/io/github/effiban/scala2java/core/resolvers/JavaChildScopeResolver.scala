package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.classifiers.ObjectClassifier
import io.github.effiban.scala2java.core.contexts.JavaChildScopeContext
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.Defn

trait JavaChildScopeResolver {
  def resolve(context: JavaChildScopeContext): JavaScope
}

private[resolvers] class JavaChildScopeResolverImpl(objectClassifier: ObjectClassifier) extends JavaChildScopeResolver {

  def resolve(context: JavaChildScopeContext): JavaScope = {
    import context._

    (scalaTree, javaTreeType) match {
      case (objectDef: Defn.Object, _) if objectClassifier.isStandalone(objectDef) => JavaScope.UtilityClass
      case (_, JavaTreeType.Package) => JavaScope.Package
      case (_, JavaTreeType.Class | JavaTreeType.Record) => JavaScope.Class
      case (_, JavaTreeType.Enum) => JavaScope.Enum
      case (_, JavaTreeType.Interface) => JavaScope.Interface
      case _ => JavaScope.Unknown
    }
  }
}

object JavaChildScopeResolver extends JavaChildScopeResolverImpl(ObjectClassifier)
