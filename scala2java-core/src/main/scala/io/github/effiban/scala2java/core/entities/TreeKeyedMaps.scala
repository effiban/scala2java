package io.github.effiban.scala2java.core.entities

import scala.meta.Tree

object TreeKeyedMaps {

  def get[K <: Tree, V](map: Map[K, V], key: K): V = {
    map.find { case (aKey, _) => aKey.structure == key.structure }
      .map(_._2)
      .getOrElse(throw new IllegalStateException(s"No value defined for key: $key"))
  }
}
