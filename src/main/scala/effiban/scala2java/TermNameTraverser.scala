package effiban.scala2java

import scala.meta.Term

trait TermNameTraverser extends ScalaTreeTraverser[Term.Name]

private[scala2java] class TermNameTraverserImpl(implicit javaEmitter: JavaEmitter) extends TermNameTraverser {

  import javaEmitter._

  override def traverse(name: Term.Name): Unit = {
    emit(toJavaName(name))
  }

  private def toJavaName(termName: Term.Name) = {
    //TODO - translate built-in Scala method names to Java equivalents
    termName.value
  }
}

object TermNameTraverser extends TermNameTraverserImpl
