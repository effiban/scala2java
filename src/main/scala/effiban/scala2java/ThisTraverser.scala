package effiban.scala2java

import scala.meta.Name
import scala.meta.Term.This

trait ThisTraverser extends ScalaTreeTraverser[This]

private[scala2java] class ThisTraverserImpl(nameTraverser: => NameTraverser)
                                           (implicit javaEmitter: JavaEmitter) extends ThisTraverser {

  import javaEmitter._

  override def traverse(`this`: This): Unit = {
    `this`.qual match {
      case Name.Anonymous() =>
      case name =>
        nameTraverser.traverse(name)
        emit(".")
    }
    emit("this")
  }
}

object ThisTraverser extends ThisTraverserImpl(NameTraverser)
