package io.github.effiban.scala2java.core.entities

import scala.meta.Tree

object TreeKeyedMap {

  def apply[K <: Tree, V](map: Map[K, V], key: K): V = {
      get(map, key).getOrElse(throw new IllegalStateException(s"No value defined for key: $key"))
  }

  def get[K <: Tree, V](map: Map[K, V], key: K): Option[V] = {
    map.find { case (aKey, _) => aKey.structure == key.structure }.map(_._2)
  }
}
