package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.renderers.PatListRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.transformers.{DefnValToDeclVarTransformer, DefnValTransformer}

import scala.meta.Defn

trait DefnValTraverser {
  def traverse(valDef: Defn.Val, context: StatContext = StatContext()): Unit
}

private[traversers] class DefnValTraverserImpl(modListTraverser: => ModListTraverser,
                                               defnValOrVarTypeTraverser: => DefnValOrVarTypeTraverser,
                                               patTraverser: => PatTraverser,
                                               patListRenderer: => PatListRenderer,
                                               expressionTermTraverser: => TermTraverser,
                                               declVarTraverser: => DeclVarTraverser,
                                               defnValToDeclVarTransformer: DefnValToDeclVarTransformer,
                                               defnValTransformer: DefnValTransformer)
                                              (implicit javaWriter: JavaWriter) extends DefnValTraverser {

  import javaWriter._

  //TODO if it is non-public it will be invalid in a Java interface - replace with method
  override def traverse(valDef: Defn.Val, context: StatContext = StatContext()): Unit = {
    defnValToDeclVarTransformer.transform(valDef, context.javaScope) match {
      case Some(varDecl) => declVarTraverser.traverse(varDecl, context)
      case None =>
        val transformedValDef = defnValTransformer.transform(valDef, context.javaScope)
        traverseInner(transformedValDef, context)
    }
  }

  private def traverseInner(valDef: Defn.Val, context: StatContext): Unit = {
    modListTraverser.traverse(ModifiersContext(valDef, JavaTreeType.Variable, context.javaScope))
    defnValOrVarTypeTraverser.traverse(valDef.decltpe, Some(valDef.rhs), context)
    write(" ")
    //TODO verify for non-simple case
    val traversedPats = valDef.pats.map(patTraverser.traverse)
    patListRenderer.render(traversedPats)
    write(" = ")
    expressionTermTraverser.traverse(valDef.rhs)
  }
}
