package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionExtractor.{finalResultTypeFullnameOf, finalResultTypeOf}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.findModuleSymbolOf
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTransformer.{toClassSymbol, toScalaMetaPartialDeclDef, toScalaMetaType}
import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.{Term, Type}
import scala.reflect.runtime.universe._

trait ScalaReflectionMethodSignatureInferrer {
  def inferPartialMethodSignature(qualType: Type.Ref, name: Term.Name, argTypes: List[Type]): PartialDeclDef

  def inferPartialMethodSignature(qual: Term.Ref, name: Term.Name, argTypes: List[Type]): PartialDeclDef
}

object ScalaReflectionMethodSignatureInferrer extends ScalaReflectionMethodSignatureInferrer {

  def inferPartialMethodSignature(qualType: Type.Ref, name: Term.Name, argTypes: List[Type]): PartialDeclDef = {
    toClassSymbol(qualType) match {
      case Some(qualCls) => inferPartialMethodSignature(qualCls, name, argTypes)
      case _ => PartialDeclDef()
    }
  }

  def inferPartialMethodSignature(qual: Term.Ref, name: Term.Name, argTypes: List[Type]): PartialDeclDef = {
    findModuleSymbolOf(qual.toString()) match {
      case Some(qualSymbol) => inferPartialMethodSignature(qualSymbol, name, argTypes)
      case _ => PartialDeclDef()
    }
  }

  private def inferPartialMethodSignature(qualSym: Symbol, name: Term.Name, smArgTypes: List[Type]): PartialDeclDef = {
    qualSym.info.member(TermName(name.value)).alternatives
      .collect { case method: MethodSymbol => method }
      .map(method => resolvePartialMethodSignatureIfMatches(method, smArgTypes))
      .collectFirst { case partialDeclDef: PartialDeclDef if partialDeclDef.nonEmpty => partialDeclDef }
      .getOrElse(PartialDeclDef())
  }

  private def resolvePartialMethodSignatureIfMatches(method: MethodSymbol, smArgTypes: List[Type]): PartialDeclDef = {
    if (methodMatchesScalaMetaArgs(method, smArgTypes)) resolveScalaMetaPartialMethodSignature(method) else PartialDeclDef()
  }

  private def methodMatchesScalaMetaArgs(method: MethodSymbol, smArgTypes: List[Type]): Boolean = {
    method.paramLists match {
      case List(params: List[Symbol]) => paramTypesMatchScalaMetaArgTypes(params, smArgTypes)
      case Nil if smArgTypes.isEmpty => true
      case _ => false
    }
  }

  private def paramTypesMatchScalaMetaArgTypes(params: List[Symbol], smArgTypes: List[Type]): Boolean = {
    params.size == smArgTypes.size &&
    params.indices.forall(idx => paramMatchesScalaMetaArg(params(idx), smArgTypes(idx)))
  }

  private def paramMatchesScalaMetaArg(param: Symbol, smArgType: Type): Boolean = {
    val paramTypeFullName = finalResultTypeFullnameOf(param)
    toClassSymbol(smArgType).map(_.fullName).contains(paramTypeFullName)
  }

  private def resolveScalaMetaPartialMethodSignature(method: MethodSymbol): PartialDeclDef = {
    toScalaMetaPartialDeclDef(method)
  }
}
