package effiban.scala2java.traversers

import effiban.scala2java.contexts.InitContext

import scala.meta.Init

trait InitListTraverser {
  def traverse(inits: List[Init], context: InitContext = InitContext()): Unit
}

private[traversers] class InitListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                initTraverser: => InitTraverser) extends InitListTraverser {

  override def traverse(inits: List[Init], context: InitContext = InitContext()): Unit = {
    argumentListTraverser.traverse(args = inits, argTraverser = (init: Init) => initTraverser.traverse(init, context))
  }
}
