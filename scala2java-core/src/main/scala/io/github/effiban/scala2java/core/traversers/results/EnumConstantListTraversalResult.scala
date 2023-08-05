package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Defn

@deprecated
case class EnumConstantListTraversalResult(override val tree: Defn.Var) extends PopulatedStatTraversalResult
