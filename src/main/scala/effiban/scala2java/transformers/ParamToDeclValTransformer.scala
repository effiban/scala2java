package effiban.scala2java.transformers

import effiban.scala2java.entities.TraversalConstants.UnknownType

import scala.meta.Mod.{Final, Private}
import scala.meta.{Decl, Name, Pat, Term, Type}

trait ParamToDeclValTransformer {
  def transform(param: Term.Param): Decl.Val
}

object ParamToDeclValTransformer extends ParamToDeclValTransformer {
  override def transform(param: Term.Param): Decl.Val = {
    Decl.Val(
      mods = List(Private(within = Name.Anonymous()), Final()),
      pats = List(Pat.Var(Term.Name(param.name.value))),
      decltpe = param.decltpe.getOrElse(Type.Name(UnknownType))
    )
  }
}
