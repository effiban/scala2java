package effiban.scala2java.transformers

import effiban.scala2java.entities.TraversalConstants.JavaPlaceholder

import scala.meta.{Name, Pat, Term, Type}

trait PatToTermParamTransformer {
  def transform(`pat`: Pat, maybeDefaultType: Option[Type] = None): Option[Term.Param]
}

object PatToTermParamTransformer extends PatToTermParamTransformer {
  override def transform(`pat`: Pat, maybeDefaultDeclType: Option[Type] = None): Option[Term.Param] = {
    (`pat`, maybeDefaultDeclType) match {
      case (Pat.Var(name), maybeDeclTpe) => Some(termParam(name = name, decltpe = maybeDeclTpe))
      case (Pat.Typed(Pat.Var(name), decltpe), _) => Some(termParam(name = name, decltpe = Some(decltpe)))
      case (Pat.Wildcard(), Some(declTpe)) => Some(termParam(name = Term.Name(JavaPlaceholder), decltpe = Some(declTpe)))
      case _ => None
    }
  }

  private def termParam(name: Name, decltpe: Option[Type] = None): Term.Param = {
    Term.Param(mods = Nil, name = name, decltpe = decltpe, default = None)
  }
}

