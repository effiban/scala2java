package io.github.effiban.scala2java.resolvers

import io.github.effiban.scala2java.classifiers.ModsClassifier
import io.github.effiban.scala2java.contexts.JavaModifiersContext
import io.github.effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}

import scala.meta.Defn

private[resolvers] class JavaPublicModifierResolver(modsClassifier: ModsClassifier) extends JavaExtraModifierResolver {

  override def resolve(context: JavaModifiersContext): Option[JavaModifier] = {
    if (modsClassifier.arePublic(context.scalaMods)) resolveInner(context) else None
  }

  private def resolveInner(context: JavaModifiersContext): Option[JavaModifier] = {
    import context._

    (scalaTree, javaTreeType, javaScope) match {
      case (_: Defn.Def, JavaTreeType.Method, JavaScope.Interface) => Some(JavaModifier.Default)
      // A class (ctor.) param is a member in Scala and can be 'public', but for Java we will transfer the 'public' to a generated member
      case (_, JavaTreeType.Parameter, JavaScope.Class) => None
      case (_, _, JavaScope.Package |
                  JavaScope.Sealed |
                  JavaScope.Class |
                  JavaScope.UtilityClass |
                  JavaScope.Enum) => Some(JavaModifier.Public)
      case _ => None
    }
  }
}

object JavaPublicModifierResolver extends JavaPublicModifierResolver(ModsClassifier)
