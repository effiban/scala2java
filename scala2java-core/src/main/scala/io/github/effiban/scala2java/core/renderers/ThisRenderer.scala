package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Name
import scala.meta.Term.This

trait ThisRenderer extends JavaTreeRenderer[This]

private[renderers] class ThisRendererImpl(nameRenderer: NameRenderer)
                                         (implicit javaWriter: JavaWriter) extends ThisRenderer {

  import javaWriter._

  override def render(`this`: This): Unit = {
    `this`.qual match {
      case Name.Anonymous() =>
      case name =>
        nameRenderer.render(name)
        writeQualifierSeparator()
    }
    write("this")
  }
}
