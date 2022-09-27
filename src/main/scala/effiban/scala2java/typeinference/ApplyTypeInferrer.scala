package effiban.scala2java.typeinference

import effiban.scala2java.classifiers.TypeNameClassifier

import scala.meta.{Term, Type}

trait ApplyTypeInferrer extends TypeInferrer[Term.Apply]

private[typeinference] class ApplyTypeInferrerImpl(applyTypeTypeInferrer: => ApplyTypeTypeInferrer,
                                                   termTypeInferrer: => TermTypeInferrer,
                                                   termArgsToTypeArgsInferrer: => TermArgsToTypeArgsInferrer,
                                                   typeNameClassifier: TypeNameClassifier) extends ApplyTypeInferrer {

  override def infer(termApply: Term.Apply): Option[Type] = {
    termApply match {
      case Term.Apply(applyType: Term.ApplyType, _) => applyTypeTypeInferrer.infer(applyType)
      case Term.Apply(fun, args) => termTypeInferrer.infer(fun).map {
        case typeName: Type.Name if typeNameClassifier.isParameterizedType(typeName) =>
          Type.Apply(typeName, termArgsToTypeArgsInferrer.infer(args))
        case tpe => tpe
      }
    }
  }
}