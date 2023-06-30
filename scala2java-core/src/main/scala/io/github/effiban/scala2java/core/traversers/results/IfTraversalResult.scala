package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Term.If
import scala.meta.{Lit, Term}

case class IfTraversalResult(cond: Term,
                             thenpResult: BlockTraversalResult,
                             maybeElsepResult: Option[BlockTraversalResult] = None) extends BlockStatTraversalResult {

  override val stat: If = If(
    cond = cond,
    thenp = thenpResult.block,
    elsep =  maybeElsepResult.map(_.block).getOrElse(Lit.Unit())
  )
}
