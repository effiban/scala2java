package effiban.scala2java.traversers

import scala.meta.Pat

trait PatVarTraverser extends ScalaTreeTraverser[Pat.Var]

private[traversers] class PatVarTraverserImpl(termNameTraverser: => TermNameTraverser) extends PatVarTraverser {

  // Pattern match variable, e.g. `a` in case a =>
  override def traverse(patternVar: Pat.Var): Unit = {
    termNameTraverser.traverse(patternVar.name)
  }
}

object PatVarTraverser extends PatVarTraverserImpl(TermNameTraverser)
