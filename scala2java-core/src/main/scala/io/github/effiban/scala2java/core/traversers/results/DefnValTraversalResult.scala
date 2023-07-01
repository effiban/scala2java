package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Defn

case class DefnValTraversalResult(override val tree: Defn.Val, override val javaModifiers: List[JavaModifier] = Nil)
  extends StatWithJavaModifiersTraversalResult
