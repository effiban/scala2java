package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.NameRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Name
import scala.meta.Term.Super

trait SuperTraverser extends ScalaTreeTraverser[Super]

private[traversers] class SuperTraverserImpl(nameTraverser: NameTraverser,
                                             nameRenderer: NameRenderer)
                                            (implicit javaWriter: JavaWriter) extends SuperTraverser {

  import javaWriter._

  def traverse(`super`: Super): Unit = {
    `super`.thisp match {
      case Name.Anonymous() =>
      case name =>
        val traversedName = nameTraverser.traverse(name)
        nameRenderer.render(traversedName)
        writeQualifierSeparator()
    }
    write("super")
    `super`.superp match {
      case Name.Anonymous() =>
      case name => writeComment(s"extends ${name.value}")
    }
  }
}
