package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.entities.{JavaKeyword, ListTraversalOptions}
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Name

trait PermittedSubTypeNameListTraverser {

  def traverse(permittedSubTypeNames: List[Name]): Unit
}

private[traversers] class PermittedSubTypeNameListTraverserImpl(argumentListTraverser: => ArgumentListTraverser)
                                                               (implicit javaWriter: JavaWriter) extends PermittedSubTypeNameListTraverser {

  import javaWriter._

  def traverse(permittedSubTypeNames: List[Name]): Unit = {
    writeKeyword(JavaKeyword.Permits)
    write(" ")
    argumentListTraverser.traverse(
      args = permittedSubTypeNames,
      argTraverser = (name: Name) => write(name.value),
      options = ListTraversalOptions(onSameLine = true)
    )
  }
}
