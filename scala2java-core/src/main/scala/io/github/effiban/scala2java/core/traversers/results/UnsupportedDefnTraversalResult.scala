package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Defn

@deprecated
case class UnsupportedDefnTraversalResult(override val tree: Defn, override val javaModifiers: List[JavaModifier] = Nil)
  extends DefnTraversalResult
