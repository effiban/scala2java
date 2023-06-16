package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.entities.TraversalConstants.JavaPlaceholder

import scala.meta.{Name, Pat, Term, Type}

trait PatToTermParamDesugarer {
  def desugar(`pat`: Pat): Option[Term.Param]
}

object PatToTermParamDesugarer extends PatToTermParamDesugarer {
  override def desugar(`pat`: Pat): Option[Term.Param] = `pat` match {
      case Pat.Var(name) => Some(termParam(name = name))
      case Pat.Typed(Pat.Var(name), decltpe) => Some(termParam(name = name, maybeType = Some(decltpe)))
      case Pat.Wildcard() => Some(termParam(name = Term.Name(JavaPlaceholder)))
      // TODO handle Pat.Tuple
      case _ => None
    }

  private def termParam(name: Name, maybeType: Option[Type] = None): Term.Param = {
    Term.Param(mods = Nil, name = name, decltpe = maybeType, default = None)
  }
}

