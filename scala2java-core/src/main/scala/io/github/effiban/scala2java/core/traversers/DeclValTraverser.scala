package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.renderers.{PatListRenderer, TypeRenderer}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Decl

trait DeclValTraverser {
  def traverse(valDecl: Decl.Val, context: StatContext = StatContext()): Unit
}

private[traversers] class DeclValTraverserImpl(modListTraverser: => DeprecatedModListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               typeRenderer: => TypeRenderer,
                                               patTraverser: => PatTraverser,
                                               patListRenderer: => PatListRenderer)
                                              (implicit javaWriter: JavaWriter) extends DeclValTraverser {

  import javaWriter._

  //TODO replace interface data member declaration (invalid in Java) with method declaration
  override def traverse(valDecl: Decl.Val, context: StatContext = StatContext()): Unit = {
    modListTraverser.traverse(ModifiersContext(valDecl, JavaTreeType.Variable, context.javaScope))
    val traversedType = typeTraverser.traverse(valDecl.decltpe)
    typeRenderer.render(traversedType)
    write(" ")
    //TODO - verify when not simple case
    val traversedPats = valDecl.pats.map(patTraverser.traverse)
    patListRenderer.render(traversedPats)
  }
}
