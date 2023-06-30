package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Term

case class SingleTermFunctionTraversalResult(override val function: Term.Function) extends TermFunctionTraversalResult
