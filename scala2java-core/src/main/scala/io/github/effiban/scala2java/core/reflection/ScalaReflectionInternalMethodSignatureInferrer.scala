package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionExtractor.byNameInnerTypeSymbolOf
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalClassifier.{isByNameParamType, isFunctionType, isRepeatedParamType, isTupleType}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.{isAssignableFrom, selfAndBaseClassesOf}
import io.github.effiban.scala2java.core.reflection.ScalaReflectionMethodParamMapper.mapParamsToScalaMetaArgTypes
import io.github.effiban.scala2java.core.reflection.ScalaReflectionTransformer.{toClassSymbol, toScalaMetaPartialDeclDef}
import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.{Term, Type}
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

private[reflection] object ScalaReflectionInternalMethodSignatureInferrer {

  def inferPartialMethodSignature(qualSym: Symbol, name: Term.Name, smArgTypeLists: List[List[Type]]): PartialDeclDef = {
    qualSym.info.member(TermName(name.value)).alternatives
      .collect { case method: MethodSymbol => method }
      .map(method => resolvePartialMethodSignatureIfMatches(method, smArgTypeLists))
      .collectFirst { case partialDeclDef: PartialDeclDef if partialDeclDef.nonEmpty => partialDeclDef }
      .getOrElse(PartialDeclDef())
  }

  private def resolvePartialMethodSignatureIfMatches(method: MethodSymbol, smArgTypeLists: List[List[Type]]): PartialDeclDef = {
    if (methodMatchesScalaMetaArgs(method, smArgTypeLists)) resolveScalaMetaPartialMethodSignature(method) else PartialDeclDef()
  }

  private def methodMatchesScalaMetaArgs(method: MethodSymbol, smArgTypeLists: List[List[Type]]): Boolean = {
    // If a method has multiple param lists, the number of actual arg lists cannot be greater than the number of param lists
    // - but it can be smaller, if a param list has only defaults / implicits
    smArgTypeLists.size <= method.paramLists.size &&
      method.paramLists.zipWithIndex.forall { case (params, index) =>
        val smArgTypes = if (smArgTypeLists.isDefinedAt(index)) smArgTypeLists(index) else List.empty[Type]
        paramTypesMatchScalaMetaArgTypes(method, params, smArgTypes)
      }
  }

  private def paramTypesMatchScalaMetaArgTypes(method: MethodSymbol,
                                               params: List[Symbol],
                                               smArgTypes: List[Type]): Boolean = {
    mapParamsToScalaMetaArgTypes(method, params, smArgTypes) match {
      case Right(mapping) =>
        mapping.forall { case (param, argTypes) =>
          val paramType = param.typeSignature
          argTypes.forall(argType => paramTypeMatchesScalaMetaArgType(paramType, argType))
        }
      case Left(_) => false
    }
  }

  private def paramTypeMatchesScalaMetaArgType(paramType: universe.Type, smArgType: Type): Boolean = {
    val paramTypeSym = paramType.typeSymbol
    val paramTypeArgSyms = paramType.typeArgs.map(_.typeSymbol)

    paramTypeSym match {
      case sym if isTupleType(sym) => smArgType match {
        case Type.Tuple(smArgTypeArgs) => nestedParamTypesMatchScalaMetaArgTypes(paramTypeArgSyms, smArgTypeArgs)
        case _ => false
      }
      case sym if isFunctionType(sym) => smArgType match {
        case Type.Function(smArgTypeArgs, smResultType) => nestedParamTypesMatchScalaMetaArgTypes(paramTypeArgSyms, smArgTypeArgs :+ smResultType)
        case _ => false
      }
      case sym if isByNameParamType(paramTypeSym) =>
        val innerParamSym = byNameInnerTypeSymbolOf(paramType)
        simpleParamTypeMatchesScalaMetaArgType(innerParamSym, smArgType)

      case sym if isRepeatedParamType(paramTypeSym) =>
        val innerParamSym = paramTypeArgSyms.headOption.getOrElse(NoSymbol)
        simpleParamTypeMatchesScalaMetaArgType(innerParamSym, smArgType)
      case _ => simpleParamTypeMatchesScalaMetaArgType(paramTypeSym, smArgType)
    }
  }

  private def nestedParamTypesMatchScalaMetaArgTypes(params: List[Symbol], smArgTypes: List[Type]): Boolean = {
    params.size == smArgTypes.size &&
      params.zipWithIndex.forall { case (param, index) =>
        paramTypeMatchesScalaMetaArgType(param.typeSignature, smArgTypes(index))
      }
  }

  private def simpleParamTypeMatchesScalaMetaArgType(paramTypeSym: Symbol, smArgType: Type): Boolean = {
    toClassSymbol(smArgType).exists(argType => isAssignableFrom(paramTypeSym, argType))
  }

  private def resolveScalaMetaPartialMethodSignature(method: MethodSymbol): PartialDeclDef = {
    toScalaMetaPartialDeclDef(method)
  }
}
