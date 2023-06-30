package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.traversers.results.CatchHandlerTraversalResult

import scala.meta.Case
import scala.meta.Term.Block

object TestableCatchHandlerTraversalResult {

  def apply(catchCase: Case, uncertainReturn: Boolean = false): CatchHandlerTraversalResult = {
    val bodyResult = catchCase.body match {
      case bodyBlock: Block => TestableBlockTraversalResult(block = bodyBlock, uncertainReturn = uncertainReturn)
      case aBody => throw new IllegalArgumentException(
        s"A CatchHandlerTraversalResult must contain a body which is a Block, but the input body is: $aBody"
      )
    }

    CatchHandlerTraversalResult(catchCase.pat, bodyResult) 
  }
}
