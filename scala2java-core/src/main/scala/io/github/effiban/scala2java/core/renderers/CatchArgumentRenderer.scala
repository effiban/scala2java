package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat

trait CatchArgumentRenderer extends JavaTreeRenderer[Pat]

private[renderers] class CatchArgumentRendererImpl(patRenderer: => PatRenderer)
                                                  (implicit javaWriter: JavaWriter) extends CatchArgumentRenderer {
  import javaWriter._

  override def render(arg: Pat): Unit = {
    writeStartDelimiter(Parentheses)
    renderArg(arg)
    writeEndDelimiter(Parentheses)
  }
  private def renderArg(arg: Pat): Unit = arg match {
    case typedArg: Pat.Typed => patRenderer.render(typedArg)
    // TODO support Pat.Alternative as a Java multi-catch arg when possible
    case anArg => writeComment(s"UNSUPPORTED catch argument: $anArg")
  }


}
