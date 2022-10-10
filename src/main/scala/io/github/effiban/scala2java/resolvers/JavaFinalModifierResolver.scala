package io.github.effiban.scala2java.resolvers

import io.github.effiban.scala2java.contexts.JavaModifiersContext
import io.github.effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}

import scala.meta.{Decl, Defn, Term}

object JavaFinalModifierResolver extends JavaExtraModifierResolver {

  override def resolve(context: JavaModifiersContext): Option[JavaModifier] = {
    import context._

    (scalaTree, javaTreeType, javaScope) match {
      case (_: Defn.Object, JavaTreeType.Enum, _) => None
      case (_: Defn.Object, _, _) => Some(JavaModifier.Final)
      case (_: Decl.Val | _: Defn.Val, _,  JavaScope.Class | JavaScope.UtilityClass | JavaScope.Enum | JavaScope.Block) => Some(JavaModifier.Final)
      // Can't add final in a Lambda param because it might not have an explicit type,
      // and we are not adding 'var' there either at this point since it has complicated rules
      case (_: Term.Param, _, JavaScope.LambdaSignature) => None
      case (_: Term.Param, _, _) => Some(JavaModifier.Final)
      case _ => None
    }
  }
}