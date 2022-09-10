package effiban.scala2java.predicates

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.{Lambda, Method}

import scala.meta.{Decl, Defn, Term}

trait JavaRequiresFinalModifierPredicate extends Function[JavaModifiersContext, Boolean]

object JavaRequiresFinalModifierPredicate extends JavaRequiresFinalModifierPredicate {

  override def apply(context: JavaModifiersContext): Boolean = {
    import context._

    (scalaTree, javaScope) match {
      case (_: Decl.Val | _ : Defn.Val, JavaTreeType.Class | JavaTreeType.Enum | Method | Lambda) => true
      // Can't add final in a Lambda param because it might not have an explicit type,
      // and we are not adding 'var' there either at this point since it has complicated rules
      case (_: Term.Param, JavaTreeType.Lambda) => false
      case (_: Term.Param, _) => true
      case _ => false
    }
  }
}
