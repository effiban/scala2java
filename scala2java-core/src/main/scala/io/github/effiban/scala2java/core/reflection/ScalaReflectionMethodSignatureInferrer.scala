package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionExtractor.finalResultTypeOf
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.findModuleSymbolOf
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTransformer.{toClassSymbol, toScalaMetaType}
import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.{Term, Type}
import scala.reflect.runtime.universe._

trait ScalaReflectionMethodSignatureInferrer {
  def inferPartialMethodSignature(qualType: Type.Ref, name: Term.Name): PartialDeclDef

  def inferPartialMethodSignature(qual: Term.Ref, name: Term.Name): PartialDeclDef
}

object ScalaReflectionMethodSignatureInferrer extends ScalaReflectionMethodSignatureInferrer {

  def inferPartialMethodSignature(qualType: Type.Ref, name: Term.Name): PartialDeclDef = {
    toClassSymbol(qualType) match {
      case Some(qualCls) => inferPartialMethodSignature(qualCls, name)
      case _ => PartialDeclDef()
    }
  }

  def inferPartialMethodSignature(qual: Term.Ref, name: Term.Name): PartialDeclDef = {
    findModuleSymbolOf(qual.toString()) match {
      case Some(qualSymbol) => inferPartialMethodSignature(qualSymbol, name)
      case _ => PartialDeclDef()
    }
  }

  private def inferPartialMethodSignature(qualSym: Symbol, name: Term.Name): PartialDeclDef = {
    qualSym.info.member(TermName(name.value)).alternatives
      .collect { case method: MethodSymbol => method }
      .map(inferPartialMethodSignatureIfMatches)
      .collectFirst { case partialDeclDef: PartialDeclDef if partialDeclDef.nonEmpty => partialDeclDef }
      .getOrElse(PartialDeclDef())
  }

  private def inferPartialMethodSignatureIfMatches(method: MethodSymbol): PartialDeclDef = {
    if (methodMatchesScalaMetaArgs(method)) inferPartialMethodSignature(method) else PartialDeclDef()
  }

  private def methodMatchesScalaMetaArgs(method: MethodSymbol): Boolean = {
    method.paramLists match {
      case Nil | List(Nil) => true
      case _ => false
    }
  }

  private def inferPartialMethodSignature(method: MethodSymbol): PartialDeclDef = {
    val maybeSMReturnType = toScalaMetaType(finalResultTypeOf(method))
    PartialDeclDef(maybeReturnType = maybeSMReturnType)
  }
}
