package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.NameRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Name
import scala.meta.Term.This

trait ThisTraverser extends ScalaTreeTraverser[This]

private[traversers] class ThisTraverserImpl(nameTraverser: NameTraverser,
                                            nameRenderer: NameRenderer)
                                           (implicit javaWriter: JavaWriter) extends ThisTraverser {

  import javaWriter._

  override def traverse(`this`: This): Unit = {
    `this`.qual match {
      case Name.Anonymous() =>
      case name =>
        val traversedName = nameTraverser.traverse(name)
        nameRenderer.render(traversedName)
        writeQualifierSeparator()
    }
    write("this")
  }
}
