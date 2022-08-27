package effiban.scala2java.resolvers

import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}

import scala.meta.{Defn, Tree}

trait JavaModifierImplyingPublicResolver {

  def resolve(scalaTree: Tree, javaTreeType: JavaTreeType, javaScope: JavaTreeType): Option[JavaModifier]
}

object JavaModifierImplyingPublicResolver extends JavaModifierImplyingPublicResolver {

  override def resolve(scalaTree: Tree, javaTreeType: JavaTreeType, javaScope: JavaTreeType): Option[JavaModifier] = {
    (scalaTree, javaTreeType, javaScope) match {
      case (_: Defn.Def, JavaTreeType.Method, JavaTreeType.Interface) => Some(JavaModifier.Default)
      // A class (ctor.) param is a member in Scala and can be 'public', but for Java we will transfer the 'public' to a generated member
      case (_, JavaTreeType.Parameter, JavaTreeType.Class) => None
      case (_, _, JavaTreeType.Package | JavaTreeType.Class) => Some(JavaModifier.Public)
      case _ => None
    }
  }
}
