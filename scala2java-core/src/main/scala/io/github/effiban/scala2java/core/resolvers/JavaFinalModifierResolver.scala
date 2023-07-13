package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.{Decl, Defn, Term}

object JavaFinalModifierResolver extends JavaExtraModifierResolver {

  override def resolve(context: ModifiersContext): Option[JavaModifier] = {
    import context._

    (scalaTree, javaTreeType, javaScope) match {
      case (_: Defn.Object, JavaTreeType.Enum, _) => None
      case (_: Defn.Object, _, _) => Some(JavaModifier.Final)
      case (_: Decl.Val | _: Defn.Val, _,  JavaScope.Class | JavaScope.UtilityClass | JavaScope.Enum | JavaScope.Block) => Some(JavaModifier.Final)
      // TODO interface method declaration params do not need the 'final' either (implicitly 'final')
      case (_: Term.Param, _, JavaScope.MethodSignature) => Some(JavaModifier.Final)
      case _ => None
    }
  }
}
