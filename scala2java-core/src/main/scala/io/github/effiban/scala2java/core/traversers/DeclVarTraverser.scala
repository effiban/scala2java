package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.renderers.TypeRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Decl

trait DeclVarTraverser {
  def traverse(varDecl: Decl.Var, context: StatContext = StatContext()): Unit
}

private[traversers] class DeclVarTraverserImpl(modListTraverser: => ModListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               typeRenderer: => TypeRenderer,
                                               patListTraverser: => PatListTraverser)
                                              (implicit javaWriter: JavaWriter) extends DeclVarTraverser {

  import javaWriter._

  //TODO replace mutable interface data member (invalid in Java) with accessor / mutator methods
  override def traverse(varDecl: Decl.Var, context: StatContext = StatContext()): Unit = {
    modListTraverser.traverse(ModifiersContext(varDecl, JavaTreeType.Variable, context.javaScope))
    val traversedType = typeTraverser.traverse(varDecl.decltpe)
    typeRenderer.render(traversedType)
    write(" ")
    //TODO - verify when not simple case
    patListTraverser.traverse(varDecl.pats)
  }
}
