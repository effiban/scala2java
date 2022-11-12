package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Decl

trait DeclValTraverser {
  def traverse(valDecl: Decl.Val, context: StatContext = StatContext()): Unit
}

private[traversers] class DeclValTraverserImpl(modListTraverser: => ModListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser)
                                              (implicit javaWriter: JavaWriter) extends DeclValTraverser {

  import javaWriter._

  //TODO replace interface data member declaration (invalid in Java) with method declaration
  override def traverse(valDecl: Decl.Val, context: StatContext = StatContext()): Unit = {
    modListTraverser.traverse(ModifiersContext(valDecl, JavaTreeType.Variable, context.javaScope))
    typeTraverser.traverse(valDecl.decltpe)
    write(" ")
    //TODO - verify when not simple case
    patListTraverser.traverse(valDecl.pats)
  }
}