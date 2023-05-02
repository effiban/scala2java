package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.{Super, This}

trait DefaultTermRefRenderer extends TermRefRenderer

private[renderers] class DefaultTermRefRendererImpl(thisRenderer: ThisRenderer,
                                                    superRenderer: SuperRenderer,
                                                    termNameRenderer: TermNameRenderer,
                                                    defaultTermSelectRenderer: => DefaultTermSelectRenderer)
                                                   (implicit javaWriter: JavaWriter) extends DefaultTermRefRenderer {

  import javaWriter._

  override def render(termRef: Term.Ref): Unit = termRef match {
    case `this`: This => thisRenderer.render(`this`)
    case `super`: Super => superRenderer.render(`super`)
    case termName: Term.Name => termNameRenderer.render(termName)
    case termSelect: Term.Select => defaultTermSelectRenderer.render(termSelect)
    case _ => writeComment(s"UNSUPPORTED Term.Ref in a Path context: $termRef")
  }
}
