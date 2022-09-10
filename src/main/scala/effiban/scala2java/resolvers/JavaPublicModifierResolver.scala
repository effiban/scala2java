package effiban.scala2java.resolvers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}

import scala.meta.Defn

trait JavaPublicModifierResolver {

  def resolve(context: JavaModifiersContext): Option[JavaModifier]
}

object JavaPublicModifierResolver extends JavaPublicModifierResolver {

  override def resolve(context: JavaModifiersContext): Option[JavaModifier] = {
    import context._

    (scalaTree, javaTreeType, javaScope) match {
      case (_: Defn.Def, JavaTreeType.Method, JavaTreeType.Interface) => Some(JavaModifier.Default)
      // A class (ctor.) param is a member in Scala and can be 'public', but for Java we will transfer the 'public' to a generated member
      case (_, JavaTreeType.Parameter, JavaTreeType.Class) => None
      case (_, _, JavaTreeType.Package | JavaTreeType.Class | JavaTreeType.Enum) => Some(JavaModifier.Public)
      case _ => None
    }
  }
}
