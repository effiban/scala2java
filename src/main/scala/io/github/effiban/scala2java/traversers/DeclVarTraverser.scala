package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.entities.JavaTreeType
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Decl

trait DeclVarTraverser {
  def traverse(varDecl: Decl.Var, context: StatContext = StatContext()): Unit
}

private[traversers] class DeclVarTraverserImpl(modListTraverser: => ModListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser)
                                              (implicit javaWriter: JavaWriter) extends DeclVarTraverser {

  import javaWriter._

  //TODO replace mutable interface data member (invalid in Java) with accessor / mutator methods
  override def traverse(varDecl: Decl.Var, context: StatContext = StatContext()): Unit = {
    modListTraverser.traverse(ModifiersContext(varDecl, JavaTreeType.Variable, context.javaScope))
    typeTraverser.traverse(varDecl.decltpe)
    write(" ")
    //TODO - verify when not simple case
    patListTraverser.traverse(varDecl.pats)
  }
}
