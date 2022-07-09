package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Name
import scala.meta.Term.This

trait ThisTraverser extends ScalaTreeTraverser[This]

private[traversers] class ThisTraverserImpl(nameTraverser: => NameTraverser)
                                           (implicit javaWriter: JavaWriter) extends ThisTraverser {

  import javaWriter._

  override def traverse(`this`: This): Unit = {
    `this`.qual match {
      case Name.Anonymous() =>
      case name =>
        nameTraverser.traverse(name)
        write(".")
    }
    write("this")
  }
}

object ThisTraverser extends ThisTraverserImpl(NameTraverser)
