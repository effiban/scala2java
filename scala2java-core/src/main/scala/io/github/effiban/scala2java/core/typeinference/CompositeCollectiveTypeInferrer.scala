package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeNameValues.ScalaAny

import scala.meta.Type

trait CompositeCollectiveTypeInferrer {

  def infer(types: List[Option[Type]]): Type
}

private[typeinference] class CompositeCollectiveTypeInferrerImpl(collectiveTypeInferrer: => CollectiveTypeInferrer) extends CompositeCollectiveTypeInferrer {

  override def infer(maybeTypes: List[Option[Type]]): Type = {
    val tupleTypes = maybeTypes.collect { case Some(typeTuple: Type.Tuple) => typeTuple }
    if (tupleTypes.nonEmpty && tupleTypes.size == maybeTypes.size) {
      collectiveTypeInferrer.inferTuple(tupleTypes)
    } else {
      collectiveTypeInferrer.inferScalar(maybeTypes).getOrElse(Type.Name(ScalaAny))
    }
  }
}