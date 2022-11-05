package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaScope, JavaTreeType}

object JavaStaticModifierResolver extends JavaExtraModifierResolver {

  override def resolve(context: ModifiersContext): Option[JavaModifier] = {
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
