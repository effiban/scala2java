package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.JavaKeyword.Permits
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Name

trait PermittedSubTypeNameListRenderer {

  def render(permittedSubTypeNames: List[Name]): Unit
}

private[renderers] class PermittedSubTypeNameListRendererImpl(argumentListRenderer: => ArgumentListRenderer)
                                                             (implicit javaWriter: JavaWriter) extends PermittedSubTypeNameListRenderer {

  import javaWriter._

  def render(permittedSubTypeNames: List[Name]): Unit = {
    writeKeyword(Permits)
    write(" ")
    argumentListRenderer.render(
      args = permittedSubTypeNames,
      argRenderer = (name: Name, _) => write(name.value),
      context = ArgumentListContext(options = ListTraversalOptions(onSameLine = true))
    )
  }
}
