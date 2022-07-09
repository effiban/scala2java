package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Name
import scala.meta.Term.Super

trait SuperTraverser extends ScalaTreeTraverser[Super]

private[traversers] class SuperTraverserImpl(nameTraverser: => NameTraverser)
                                            (implicit javaWriter: JavaWriter) extends SuperTraverser {

  import javaWriter._

  def traverse(`super`: Super): Unit = {
    `super`.thisp match {
      case Name.Anonymous() =>
      case name =>
        nameTraverser.traverse(name)
        write(".")
    }
    write("super")
    `super`.superp match {
      case Name.Anonymous() =>
      case name => writeComment(s"extends ${name.value}")
    }
  }
}

object SuperTraverser extends SuperTraverserImpl(NameTraverser)
