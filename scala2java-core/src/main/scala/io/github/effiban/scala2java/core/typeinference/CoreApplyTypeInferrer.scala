package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.TypeNameClassifier
import io.github.effiban.scala2java.spi.typeinferrers.{ApplyTypeInferrer, ApplyTypeTypeInferrer}

import scala.meta.{Term, Type}

private[typeinference] class CoreApplyTypeInferrer(applyTypeTypeInferrer: => ApplyTypeTypeInferrer,
                                                   termTypeInferrer: => TermTypeInferrer,
                                                   compositeCollectiveTypeInferrer: => CompositeCollectiveTypeInferrer,
                                                   typeNameClassifier: TypeNameClassifier) extends ApplyTypeInferrer {

  override def infer(termApply: Term.Apply, maybeArgTypes: List[Option[Type]] = Nil): Option[Type] = {
    termApply match {
      case Term.Apply(applyType: Term.ApplyType, _) => applyTypeTypeInferrer.infer(applyType)
      case Term.Apply(fun, _) => termTypeInferrer.infer(fun).map {
        case typeName: Type.Name if typeNameClassifier.isParameterizedType(typeName) => Type.Apply(typeName, inferCollectiveTypes(maybeArgTypes))
        case tpe => tpe
      }
    }
  }

  private def inferCollectiveTypes(maybeTypes: List[Option[Type]]): List[Type] = {
    compositeCollectiveTypeInferrer.infer(maybeTypes) match {
      case typeTuple: Type.Tuple => typeTuple.args
      case tpe: Type => List(tpe)
    }
  }
}