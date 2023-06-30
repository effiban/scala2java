package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Term

case class BlockTermFunctionTraversalResult(params: List[Term.Param] = Nil, bodyResult: BlockTraversalResult)
  extends TermFunctionTraversalResult {
  override val function: Term.Function = Term.Function(params, bodyResult.block)
}
