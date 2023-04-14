package io.github.effiban.scala2java.core.predicates

trait CompositeAtLeastOneTruePredicate0[T] extends (T => Boolean) {

  protected val predicates: List[T => Boolean]

  override def apply(obj: T): Boolean = predicates.exists(_.apply(obj))
}
