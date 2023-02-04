package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.JavaKeyword.Permits
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Name

trait PermittedSubTypeNameListTraverser {

  def traverse(permittedSubTypeNames: List[Name]): Unit
}

private[traversers] class PermittedSubTypeNameListTraverserImpl(argumentListTraverser: => ArgumentListTraverser)
                                                               (implicit javaWriter: JavaWriter) extends PermittedSubTypeNameListTraverser {

  import javaWriter._

  def traverse(permittedSubTypeNames: List[Name]): Unit = {
    writeKeyword(Permits)
    write(" ")
    argumentListTraverser.traverse(
      args = permittedSubTypeNames,
      argTraverser = (name: Name, _) => write(name.value),
      context = ArgumentListContext(options = ListTraversalOptions(onSameLine = true))
    )
  }
}
