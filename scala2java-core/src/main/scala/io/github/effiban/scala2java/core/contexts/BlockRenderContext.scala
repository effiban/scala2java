package io.github.effiban.scala2java.core.contexts

import scala.meta.Init

// The 'init' param is passed by constructors, whose first statement must be a call to super or other ctor.
// 'Init' does not inherit from 'Stat' so we can't add it to the Block
case class BlockRenderContext(lastStatContext: BlockStatRenderContext = SimpleBlockStatRenderContext(),
                              maybeInit: Option[Init] = None)
