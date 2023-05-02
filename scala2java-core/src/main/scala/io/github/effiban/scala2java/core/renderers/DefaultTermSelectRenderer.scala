package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait DefaultTermSelectRenderer extends TermSelectRenderer

private[renderers] class DefaultTermSelectRendererImpl(defaultTermRefRenderer: => DefaultTermRefRenderer,
                                                       termNameRenderer: TermNameRenderer)
                                                      (implicit javaWriter: JavaWriter) extends DefaultTermSelectRenderer {

  import javaWriter._

  // A qualified name in a stable, non-expression and non-function context
  override def render(select: Term.Select): Unit = {
    renderQualifier(select.qual)
    writeQualifierSeparator()
    termNameRenderer.render(select.name)
  }

  private def renderQualifier(qual: Term): Unit = {
    qual match {
      case aQual: Term.Ref => defaultTermRefRenderer.render(aQual)
      case aQual => writeComment(s"UNSUPPORTED qualifier in stable path context: $aQual")
    }
  }
}


