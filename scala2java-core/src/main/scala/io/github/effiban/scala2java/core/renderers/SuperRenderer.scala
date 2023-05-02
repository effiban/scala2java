package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Name
import scala.meta.Term.Super

trait SuperRenderer extends JavaTreeRenderer[Super]

private[renderers] class SuperRendererImpl(nameRenderer: NameRenderer)
                                          (implicit javaWriter: JavaWriter) extends SuperRenderer {

  import javaWriter._

  def render(`super`: Super): Unit = {
    `super`.thisp match {
      case Name.Anonymous() =>
      case name =>
        nameRenderer.render(name)
        writeQualifierSeparator()
    }
    write("super")
    `super`.superp match {
      case Name.Anonymous() =>
      case name => writeComment(s"extends ${name.value}")
    }
  }
}
