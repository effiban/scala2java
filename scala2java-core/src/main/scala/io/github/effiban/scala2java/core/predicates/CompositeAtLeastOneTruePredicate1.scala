package io.github.effiban.scala2java.core.predicates

trait CompositeAtLeastOneTruePredicate1[T, A] extends ((T, A) => Boolean) {

  protected val predicates: List[(T, A) => Boolean]

  override def apply(obj: T, arg: A): Boolean = predicates.exists(_.apply(obj, arg))
}
