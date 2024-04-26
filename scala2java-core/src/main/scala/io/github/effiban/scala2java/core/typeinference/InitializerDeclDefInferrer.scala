package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.ParameterizedInitializerNameTypeMapping
import io.github.effiban.scala2java.spi.entities.PartialDeclDef

import scala.meta.{Term, Type}

trait InitializerDeclDefInferrer {
  def inferByAppliedTypes(qual: Term.Select, appliedTypes: List[Type], numArgs: Int): PartialDeclDef

  def inferByArgTypes(qual: Term.Select, maybeArgTypes: List[Option[Type]]): PartialDeclDef
}

private[typeinference] class InitializerDeclDefInferrerImpl(compositeCollectiveTypeInferrer: => CompositeCollectiveTypeInferrer,
                                                            parameterizedInitializerNameTypeMapping: ParameterizedInitializerNameTypeMapping)
  extends InitializerDeclDefInferrer {

  import parameterizedInitializerNameTypeMapping._

  override def inferByAppliedTypes(qual: Term.Select, appliedTypes: List[Type], numArgs: Int): PartialDeclDef = {

    typeInitializedBy(qual) match {
      case Some(parameterizedType) => PartialDeclDef(
        maybeParamTypes = List.fill(numArgs)(Some(inferParamTypeFromAppliedTypes(appliedTypes))),
        maybeReturnType = Some(Type.Apply(parameterizedType, appliedTypes))
      )
      case None => PartialDeclDef()
    }
  }

  override def inferByArgTypes(qual: Term.Select, maybeArgTypes: List[Option[Type]]): PartialDeclDef = {

    typeInitializedBy(qual) match {
      case Some(parameterizedType) =>
        val paramType = inferParamTypeFromArgTypes(maybeArgTypes)
        val appliedTypes = inferAppliedTypesFromParamType(paramType)
        PartialDeclDef(
          maybeParamTypes = List.fill(maybeArgTypes.size)(Some(paramType)),
          maybeReturnType = Some(Type.Apply(parameterizedType, appliedTypes))
        )
      case None => PartialDeclDef()
    }
  }

  private def inferParamTypeFromArgTypes(maybeArgTypes: List[Option[Type]]) = {
    compositeCollectiveTypeInferrer.infer(maybeArgTypes)
  }

  private def inferAppliedTypesFromParamType(paramType: Type) = {
    paramType match {
      case typeTuple: Type.Tuple => typeTuple.args
      case tpe: Type => List(tpe)
    }
  }

  private def inferParamTypeFromAppliedTypes(typeArgs: List[Type]) = typeArgs match {
    case List(typeArg) => typeArg
    case tpeArgs => Type.Tuple(tpeArgs)
  }
}