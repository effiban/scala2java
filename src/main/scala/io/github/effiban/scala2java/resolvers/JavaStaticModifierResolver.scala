package io.github.effiban.scala2java.resolvers

import io.github.effiban.scala2java.contexts.JavaModifiersContext
import io.github.effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}

object JavaStaticModifierResolver extends JavaExtraModifierResolver {

  override def resolve(context: JavaModifiersContext): Option[JavaModifier] = {
    import context._

    (javaTreeType, javaScope) match {
      case (JavaTreeType.Class |
            JavaTreeType.Record |
            JavaTreeType.Enum |
            JavaTreeType.Interface |
            JavaTreeType.Method |
            JavaTreeType.Variable, JavaScope.UtilityClass) => Some(JavaModifier.Static)
      case _ => None
    }
  }
}
