package effiban.scala2java.predicates

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.{Lambda, Method}
import effiban.scala2java.resolvers.JavaModifiersResolverParams

import scala.meta.{Decl, Defn, Term}

trait JavaRequiresFinalModifierPredicate extends Function[JavaModifiersResolverParams, Boolean]

object JavaRequiresFinalModifierPredicate extends JavaRequiresFinalModifierPredicate {

  override def apply(params: JavaModifiersResolverParams): Boolean = {
    import params._

    (scalaTree, javaScope) match {
      case (_: Decl.Val | _ : Defn.Val, JavaTreeType.Class | Method | Lambda) => true
      // Can't add final in a Lambda param because it might not have an explicit type,
      // and we are not adding 'var' there either at this point since it has complicated rules
      case (_: Term.Param, JavaTreeType.Lambda) => false
      case (_: Term.Param, _) => true
      case _ => false
    }
  }
}
