package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.{Case, Pat}

trait CaseTraverser extends ScalaTreeTraverser[Case]

private[traversers] class CaseTraverserImpl(patTraverser: => PatTraverser,
                                            termTraverser: => TermTraverser)
                                           (implicit javaWriter: JavaWriter) extends CaseTraverser {

  import javaWriter._

  def traverse(`case`: Case): Unit = {
    traversePat(`case`.pat)
    `case`.cond.foreach(cond => {
      write(" && ")
      termTraverser.traverse(cond)
    })
    writeArrow()
    termTraverser.traverse(`case`.body)
    writeStatementEnd()
  }

  private def traversePat(pat: Pat): Unit = {
    pat match {
      // An wildcard by itself is the default case, which must be named "default" in Java
      case _ : Pat.Wildcard => write("default")
      case aPat =>
        write("case ")
        patTraverser.traverse(aPat)
    }
  }
}
