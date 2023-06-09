package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Case, Pat}

trait CaseRenderer extends JavaTreeRenderer[Case]

private[renderers] class CaseRendererImpl(patRenderer: => PatRenderer,
                                          expressionTermRenderer: => ExpressionTermRenderer)
                                         (implicit javaWriter: JavaWriter) extends CaseRenderer {

  import javaWriter._

  def render(`case`: Case): Unit = {
    renderPat(`case`.pat)
    `case`.cond.foreach(cond => {
      write(" && ")
      expressionTermRenderer.render(cond)
    })
    writeArrow()
    expressionTermRenderer.render(`case`.body)
    writeStatementEnd()
  }

  private def renderPat(pat: Pat): Unit = {
    pat match {
      // An wildcard by itself is the default case, which must be named "default" in Java
      case _: Pat.Wildcard => write("default")
      case aPat =>
        write("case ")
        patRenderer.render(aPat)
    }
  }
}
