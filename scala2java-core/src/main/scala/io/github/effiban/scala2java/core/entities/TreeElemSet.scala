package io.github.effiban.scala2java.core.entities

import scala.meta.Tree

object TreeElemSet {

  def apply[T <: Tree](set: Set[T], elem: T): T = {
      get(set, elem).getOrElse(throw new IllegalStateException(s"No such elem: $elem"))
  }

  def contains[T <: Tree](set: Set[T], elem: T): Boolean = set.exists(_.structure == elem.structure)

  def get[T <: Tree](set: Set[T], elem: T): Option[T] = set.find(_.structure == elem.structure)
}
