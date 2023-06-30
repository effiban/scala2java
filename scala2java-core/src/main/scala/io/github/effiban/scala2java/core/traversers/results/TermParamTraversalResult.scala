package io.github.effiban.scala2java.core.traversers.results

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.Term

case class TermParamTraversalResult(termParam: Term.Param, javaModifiers: List[JavaModifier] = Nil)
