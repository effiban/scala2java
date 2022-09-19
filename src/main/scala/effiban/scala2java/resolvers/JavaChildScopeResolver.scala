package effiban.scala2java.resolvers

import effiban.scala2java.classifiers.ObjectClassifier
import effiban.scala2java.contexts.JavaChildScopeContext
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.{JavaScope, JavaTreeType}

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
