package effiban.scala2java.traversers

import scala.meta.Init

trait InitListTraverser {
  def traverse(inits: List[Init], ignoreArgs: Boolean = false): Unit
}

private[traversers] class InitListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                initTraverser: => InitTraverser) extends InitListTraverser {

  override def traverse(inits: List[Init], ignoreArgs: Boolean = false): Unit = {
    argumentListTraverser.traverse(args = inits, argTraverser = (init: Init) => initTraverser.traverse(init, ignoreArgs))
  }
}
