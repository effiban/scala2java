package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.JavaKeyword.Permits
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.renderers.ArgumentListRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Name

trait PermittedSubTypeNameListTraverser {

  def traverse(permittedSubTypeNames: List[Name]): Unit
}

private[traversers] class PermittedSubTypeNameListTraverserImpl(argumentListRenderer: => ArgumentListRenderer)
                                                               (implicit javaWriter: JavaWriter) extends PermittedSubTypeNameListTraverser {

  import javaWriter._

  def traverse(permittedSubTypeNames: List[Name]): Unit = {
    writeKeyword(Permits)
    write(" ")
    argumentListRenderer.render(
      args = permittedSubTypeNames,
      argRenderer = (name: Name, _) => write(name.value),
      context = ArgumentListContext(options = ListTraversalOptions(onSameLine = true))
    )
  }
}
