package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer

import scala.meta.Type

private[typeinference] trait CompositeCoreAndOthersTypeInferrer[T] extends TypeInferrer[T] {

  protected def coreInferrer(): TypeInferrer[T]

  protected val otherInferrers: List[TypeInferrer[T]]

  private lazy val inferrers: List[TypeInferrer[T]] = otherInferrers :+ coreInferrer()

  override def infer(obj: T): Option[Type] = {
    inferrers.foldLeft[Option[Type]](None)((maybeType, inferrer) => maybeType.orElse(inferrer.infer(obj)))
  }
}