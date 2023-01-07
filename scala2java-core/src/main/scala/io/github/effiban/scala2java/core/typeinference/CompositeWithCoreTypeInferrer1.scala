package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer1

import scala.meta.Type

private[typeinference] trait CompositeWithCoreTypeInferrer1[T, A] extends TypeInferrer1[T, A] {

  protected def coreInferrer(): TypeInferrer1[T, A]

  protected val otherInferrers: List[TypeInferrer1[T, A]]

  private lazy val inferrers: List[TypeInferrer1[T, A]] = otherInferrers :+ coreInferrer()

  override def infer(obj: T, arg: A): Option[Type] = {
    inferrers.foldLeft[Option[Type]](None)((maybeType, inferrer) => maybeType.orElse(inferrer.infer(obj, arg)))
  }
}