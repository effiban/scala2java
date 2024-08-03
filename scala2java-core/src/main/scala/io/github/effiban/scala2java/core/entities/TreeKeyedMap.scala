package io.github.effiban.scala2java.core.entities

import scala.meta.Tree

object TreeKeyedMap {

  def apply[K <: Tree, V](mapLike: Iterable[(K, V)], key: K): V = {
    get(mapLike, key).getOrElse(throw new IllegalStateException(s"No value defined for key: $key"))
  }

  def get[K <: Tree, V](mapLike: Iterable[(K, V)], key: K): Option[V] = {
    mapLike.find { case (aKey, _) => aKey.structure == key.structure }.map(_._2)
  }
}
