package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermNameTraverser extends ScalaTreeTraverser[Term.Name]

private[traversers] class TermNameTraverserImpl(implicit javaWriter: JavaWriter) extends TermNameTraverser {

  import javaWriter._

  override def traverse(name: Term.Name): Unit = {
    write(toJavaName(name))
  }

  private def toJavaName(termName: Term.Name) = {
    //TODO - translate built-in Scala method names to Java equivalents
    termName.value
  }
}

object TermNameTraverser extends TermNameTraverserImpl
