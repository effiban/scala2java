package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer0

import scala.meta.Type

private[typeinference] trait CompositeWithCoreTypeInferrer0[T] extends TypeInferrer0[T] {

  protected def coreInferrer(): TypeInferrer0[T]

  protected val otherInferrers: List[TypeInferrer0[T]]

  private lazy val inferrers: List[TypeInferrer0[T]] = otherInferrers :+ coreInferrer()

  override def infer(obj: T): Option[Type] = {
    inferrers.foldLeft[Option[Type]](None)((maybeType, inferrer) => maybeType.orElse(inferrer.infer(obj)))
  }
}