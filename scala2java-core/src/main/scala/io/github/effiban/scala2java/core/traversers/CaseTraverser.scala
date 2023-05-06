package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.PatRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Case, Pat}

trait CaseTraverser extends ScalaTreeTraverser[Case]

private[traversers] class CaseTraverserImpl(patTraverser: => PatTraverser,
                                            patRenderer: => PatRenderer,
                                            expressionTermTraverser: => TermTraverser)
                                           (implicit javaWriter: JavaWriter) extends CaseTraverser {

  import javaWriter._

  def traverse(`case`: Case): Unit = {
    traversePat(`case`.pat)
    `case`.cond.foreach(cond => {
      write(" && ")
      expressionTermTraverser.traverse(cond)
    })
    writeArrow()
    expressionTermTraverser.traverse(`case`.body)
    writeStatementEnd()
  }

  private def traversePat(pat: Pat): Unit = {
    pat match {
      // An wildcard by itself is the default case, which must be named "default" in Java
      case _ : Pat.Wildcard => write("default")
      case aPat =>
        write("case ")
        val traversedPat = patTraverser.traverse(aPat)
        patRenderer.render(traversedPat)
    }
  }
}
