package effiban.scala2java.resolvers

import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}

trait JavaModifierImplyingPublicResolver {

  def resolve(javaTreeType: JavaTreeType, javaScope: JavaTreeType, hasBody: Boolean = false): Option[JavaModifier]
}

object JavaModifierImplyingPublicResolver extends JavaModifierImplyingPublicResolver {

  override def resolve(javaTreeType: JavaTreeType, javaScope: JavaTreeType, hasBody: Boolean = false): Option[JavaModifier] = {
    (javaTreeType, javaScope, hasBody) match {
      case (JavaTreeType.Method, JavaTreeType.Interface, true) => Some(JavaModifier.Default)
      case (JavaTreeType.Interface, _, _) => Some(JavaModifier.Public)
      case (_, JavaTreeType.Interface, _) => None
      case _ => Some(JavaModifier.Public)
    }
  }
}
