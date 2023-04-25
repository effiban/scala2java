package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.ThisRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.{ApplyUnary, Super, This}

private[traversers] class ExpressionTermRefTraverser(thisTraverser: ThisTraverser,
                                                     thisRenderer: ThisRenderer,
                                                     superTraverser: => SuperTraverser,
                                                     termNameTraverser: => TermNameTraverser,
                                                     termSelectTraverser: => ExpressionTermSelectTraverser,
                                                     applyUnaryTraverser: => ApplyUnaryTraverser)
                                                    (implicit javaWriter: JavaWriter) extends TermRefTraverser {

  import javaWriter._

  override def traverse(termRef: Term.Ref): Unit = termRef match {
    case `this`: This =>
      val traversedThis = thisTraverser.traverse(`this`)
      thisRenderer.render(traversedThis)
    case `super`: Super => superTraverser.traverse(`super`)
    case termName: Term.Name => termNameTraverser.traverse(termName)
    case termSelect: Term.Select => termSelectTraverser.traverse(termSelect)
    case applyUnary: ApplyUnary => applyUnaryTraverser.traverse(applyUnary)
    case _ => writeComment(s"UNSUPPORTED: $termRef")
  }
}
