package effiban.scala2java.typeinference

import scala.meta.Term.TryWithHandler
import scala.meta.{Lit, Type}

trait TryWithHandlerTypeInferrer extends TypeInferrer[TryWithHandler]

private[typeinference] class TryWithHandlerTypeInferrerImpl(termTypeInferrer: => TermTypeInferrer,
                                                            collectiveTypeInferrer: CollectiveTypeInferrer) extends TryWithHandlerTypeInferrer {

  override def infer(tryWithHandler: TryWithHandler): Option[Type] = {
    val maybeExprType = termTypeInferrer.infer(tryWithHandler.expr)
    val maybeCatchHandlerType = tryWithHandler.catchp match {
      case Lit.Unit() => Some(Type.AnonymousName())
      case _ => None
    }
    val maybeFinallyType = tryWithHandler.finallyp match {
      case Some(finallyp) => termTypeInferrer.infer(finallyp)
      case None => Some(Type.AnonymousName())
    }
    collectiveTypeInferrer.inferScalar(List(maybeExprType, maybeCatchHandlerType, maybeFinallyType))
  }
}
