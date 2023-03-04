package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TraversalConstants.UnknownType

import scala.meta.Mod.{Final, Private}
import scala.meta.{Decl, Name, Pat, Term, Type}

trait ParamToDeclValTransformer {
  def transform(param: Term.Param): Decl.Val
}

class ParamToDeclValTransformerImpl(typeByNameToSupplierTypeTransformer: TypeByNameToSupplierTypeTransformer) extends ParamToDeclValTransformer {
  override def transform(param: Term.Param): Decl.Val = {
    Decl.Val(
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

object ParamToDeclValTransformer extends ParamToDeclValTransformerImpl(TypeByNameToSupplierTypeTransformer)
