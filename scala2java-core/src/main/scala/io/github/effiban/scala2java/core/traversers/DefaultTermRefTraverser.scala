package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.{Super, This}

private[traversers] class DefaultTermRefTraverser(thisTraverser: => ThisTraverser,
                                                  superTraverser: => SuperTraverser,
                                                  termNameRenderer: TermNameRenderer,
                                                  defaultTermSelectTraverser: => DefaultTermSelectTraverser)
                                                 (implicit javaWriter: JavaWriter) extends TermRefTraverser {

  import javaWriter._

  override def traverse(termRef: Term.Ref): Unit = termRef match {
    case `this`: This => thisTraverser.traverse(`this`)
    case `super`: Super => superTraverser.traverse(`super`)
    case termName: Term.Name => termNameRenderer.render(termName)
    case termSelect: Term.Select => defaultTermSelectTraverser.traverse(termSelect)
    case _ => writeComment(s"UNSUPPORTED Term.Ref in a Path context: $termRef")
  }
}
