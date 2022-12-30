package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.TypeNameClassifier
import io.github.effiban.scala2java.spi.typeinferrers.{ApplyTypeInferrer, ApplyTypeTypeInferrer}

import scala.meta.{Term, Type}

private[typeinference] class CoreApplyTypeInferrer(applyTypeTypeInferrer: => ApplyTypeTypeInferrer,
                                                   termTypeInferrer: => TermTypeInferrer,
                                                   compositeArgListTypesInferrer: => CompositeArgListTypesInferrer,
                                                   typeNameClassifier: TypeNameClassifier) extends ApplyTypeInferrer {

  override def infer(termApply: Term.Apply): Option[Type] = {
    termApply match {
      case Term.Apply(applyType: Term.ApplyType, _) => applyTypeTypeInferrer.infer(applyType)
      case Term.Apply(fun, args) => termTypeInferrer.infer(fun).map {
        case typeName: Type.Name if typeNameClassifier.isParameterizedType(typeName) =>
          Type.Apply(typeName, compositeArgListTypesInferrer.infer(args))
        case tpe => tpe
      }
    }
  }
}