package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Pat

case class CatchHandlerTraversalResult(pat: Pat, bodyResult: BlockTraversalResult)
