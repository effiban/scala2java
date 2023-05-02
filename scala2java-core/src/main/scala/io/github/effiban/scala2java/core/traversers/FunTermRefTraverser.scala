package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.DefaultTermRefRenderer

import scala.meta.Term

private[traversers] class FunTermRefTraverser(funTermSelectTraverser: => FunTermSelectTraverser,
                                              defaultTermRefTraverser: => DefaultTermRefTraverser,
                                              defaultTermRefRenderer: => DefaultTermRefRenderer) extends TermRefTraverser {

  override def traverse(termRef: Term.Ref): Unit = termRef match {
    case termSelect: Term.Select => funTermSelectTraverser.traverse(termSelect)
    case aTermRef =>
      val traversedTermRef = defaultTermRefTraverser.traverse(aTermRef)
      defaultTermRefRenderer.render(traversedTermRef)
  }
}
