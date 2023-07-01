package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Defn

case class DefnVarTraversalResult(defnVar: Defn.Var, javaModifiers: List[JavaModifier] = Nil)
