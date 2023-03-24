package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.TypeNameClassifier
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.typeinferrers.{ApplyDeclDefInferrer, ApplyTypeTypeInferrer}

import scala.meta.{Term, Type}

private[typeinference] class CoreApplyDeclDefInferrer(applyTypeTypeInferrer: => ApplyTypeTypeInferrer,
                                                      termTypeInferrer: => TermTypeInferrer,
                                                      compositeCollectiveTypeInferrer: => CompositeCollectiveTypeInferrer,
                                                      typeNameClassifier: TypeNameClassifier) extends ApplyDeclDefInferrer {

  override def infer(termApply: Term.Apply, context: TermApplyInferenceContext): PartialDeclDef = {
    import context._

    val maybeReturnType = termApply match {
      case Term.Apply(applyType: Term.ApplyType, _) => applyTypeTypeInferrer.infer(applyType)
      case Term.Apply(fun, _) => termTypeInferrer.infer(fun).map {
        case typeName: Type.Name if typeNameClassifier.isParameterizedType(typeName) => Type.Apply(typeName, inferCollectiveTypes(maybeArgTypes))
        case tpe => tpe
      }
    }
    PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = maybeReturnType)
  }

  private def inferCollectiveTypes(maybeTypes: List[Option[Type]]): List[Type] = {
    compositeCollectiveTypeInferrer.infer(maybeTypes) match {
      case typeTuple: Type.Tuple => typeTuple.args
      case tpe: Type => List(tpe)
    }
  }
}