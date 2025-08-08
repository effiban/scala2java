package io.github.effiban.scala2java.core.reflection

import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalClassifier.isRepeatedParamType
import io.github.effiban.scala2java.core.reflection.ScalaReflectionInternalLookup.methodParamHasDefaultValue

import scala.meta.Type
import scala.reflect.runtime.universe._

private[reflection] object ScalaReflectionMethodParamMapper {

  def mapParamsToScalaMetaArgTypes(method: MethodSymbol,
                                   params: List[Symbol],
                                   smArgTypes: List[Type]): Either[Unit, Map[Symbol, List[Type]]] = {
    val missingOrMapping = params.zipWithIndex
      .map { case (param, paramIdx) => mapParamToScalaMetaArgTypes(method, param, paramIdx, smArgTypes) }
      .foldLeft[Either[Unit, Map[Symbol, List[Type]]]](Right(Map.empty))(
        (missingOrMap, missingOrEntry) => (missingOrMap, missingOrEntry) match {
          case (Right(map), Right(param -> argTypes)) => Right(map + (param -> argTypes))
          case _ => Left(())
        }
      )

    missingOrMapping.filterOrElse(_.values.flatten.size == smArgTypes.size, ())
  }

  private def mapParamToScalaMetaArgTypes(method: MethodSymbol,
                                          param: Symbol,
                                          paramIdx: Int,
                                          smArgTypes: List[Type])  = {
    (param, paramIdx) match {
      case (aParam, anIdx) if isRepeatedParamType(aParam.typeSignature.typeSymbol) => Right(aParam -> smArgTypes.drop(anIdx))
      case (aParam, anIdx) if smArgTypes.isDefinedAt(anIdx) => Right(aParam -> List(smArgTypes(anIdx)))
      case (aParam, anIdx) if methodParamHasDefaultValue(method, anIdx) => Right(aParam -> Nil)
      case _ => Left[Unit, (Symbol, List[Type])](())
    }
  }
}
