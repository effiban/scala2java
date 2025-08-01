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
      case Some(qualCls) => ScalaReflectionInternalMethodSignatureInferrer.inferPartialMethodSignature(qualCls, name, argTypes)
      case _ => PartialDeclDef()
    }
  }

  def inferPartialMethodSignature(qual: Term.Ref, name: Term.Name, argTypes: List[Type]): PartialDeclDef = {
    findModuleSymbolOf(qual.toString()) match {
      case Some(qualSymbol) => ScalaReflectionInternalMethodSignatureInferrer.inferPartialMethodSignature(qualSymbol, name, argTypes)
      case _ => PartialDeclDef()
    }
  }
}
