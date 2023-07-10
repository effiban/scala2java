package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TraversalConstants.UnknownType

import scala.meta.Mod.{Final, Private}
import scala.meta.{Decl, Name, Pat, Term, Type}

trait ParamToDeclVarTransformer {
  def transform(param: Term.Param): Decl.Var
}

class ParamToDeclVarTransformerImpl(typeByNameToSupplierTypeTransformer: TypeByNameToSupplierTypeTransformer) extends ParamToDeclVarTransformer {
  override def transform(param: Term.Param): Decl.Var = {
    Decl.Var(
      mods = List(Private(within = Name.Anonymous()), Final()),
      pats = List(Pat.Var(Term.Name(param.name.value))),
      decltpe = param.decltpe match {
        case Some(typeByName: Type.ByName) => typeByNameToSupplierTypeTransformer.transform(typeByName)
        case Some(tpe) => tpe
        case _ => Type.Name(UnknownType)
      }
    )
  }
}

object ParamToDeclVarTransformer extends ParamToDeclVarTransformerImpl(TypeByNameToSupplierTypeTransformer)
