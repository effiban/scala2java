package effiban.scala2java.transformers

import effiban.scala2java.entities.TraversalConstants.JavaPlaceholder

import scala.meta.{Name, Pat, Term, Type}

trait PatToTermParamTransformer {
  def transform(`pat`: Pat): Option[Term.Param]
}

object PatToTermParamTransformer extends PatToTermParamTransformer {
  override def transform(`pat`: Pat): Option[Term.Param] = {
    `pat` match {
      case Pat.Var(name) => Some(termParam(name = name))
      case Pat.Typed(Pat.Var(name), decltpe) => Some(termParam(name = name, decltpe = Some(decltpe)))
      case Pat.Wildcard() => Some(termParam(name = Term.Name(JavaPlaceholder)))
      case _ => None
    }
  }

  private def termParam(name: Name, decltpe: Option[Type] = None): Term.Param = {
    Term.Param(mods = Nil, name = name, decltpe = decltpe, default = None)
  }
}

