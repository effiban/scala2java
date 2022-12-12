package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.transformers.DefnValToDeclVarTransformer

import scala.meta.Defn

trait DefnValTraverser {
  def traverse(valDef: Defn.Val, context: StatContext = StatContext()): Unit
}

private[traversers] class DefnValTraverserImpl(modListTraverser: => ModListTraverser,
                                               defnValOrVarTypeTraverser: => DefnValOrVarTypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               rhsTermTraverser: => RhsTermTraverser,
                                               declVarTraverser: => DeclVarTraverser,
                                               defnValToDeclVarTransformer: DefnValToDeclVarTransformer)
                                              (implicit javaWriter: JavaWriter) extends DefnValTraverser {

  import javaWriter._

  //TODO if it is non-public it will be invalid in a Java interface - replace with method
  override def traverse(valDef: Defn.Val, context: StatContext = StatContext()): Unit = {
    defnValToDeclVarTransformer.transform(valDef, context.javaScope) match {
      case Some(varDecl) => declVarTraverser.traverse(varDecl, context)
      case None => traverseOriginal(valDef, context)
    }
  }

  private def traverseOriginal(valDef: Defn.Val, context: StatContext): Unit = {
    modListTraverser.traverse(ModifiersContext(valDef, JavaTreeType.Variable, context.javaScope))
    defnValOrVarTypeTraverser.traverse(valDef.decltpe, Some(valDef.rhs), context)
    write(" ")
    //TODO verify for non-simple case
    patListTraverser.traverse(valDef.pats)
    write(" = ")
    rhsTermTraverser.traverse(valDef.rhs)
  }
}
