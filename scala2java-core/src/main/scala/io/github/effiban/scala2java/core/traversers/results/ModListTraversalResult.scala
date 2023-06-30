package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Mod

case class ModListTraversalResult(scalaMods: List[Mod] = Nil, javaModifiers: List[JavaModifier] = Nil)
