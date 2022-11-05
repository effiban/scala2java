package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import io.github.effiban.scala2java.entities.JavaTreeType
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait DefnVarTraverser {
  def traverse(varDef: Defn.Var, context: StatContext = StatContext()): Unit
}

private[traversers] class DefnVarTraverserImpl(modListTraverser: => ModListTraverser,
                                               defnValOrVarTypeTraverser: => DefnValOrVarTypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               rhsTermTraverser: => RhsTermTraverser)
                                              (implicit javaWriter: JavaWriter) extends DefnVarTraverser {

  import javaWriter._

  //TODO replace mutable interface data member (invalid in Java) with accessor/mutator methods
  override def traverse(varDef: Defn.Var, context: StatContext = StatContext()): Unit = {
    modListTraverser.traverse(JavaModifiersContext(varDef, JavaTreeType.Variable, context.javaScope))
    defnValOrVarTypeTraverser.traverse(varDef.decltpe, varDef.rhs, context)
    write(" ")
    //TODO - verify this
    patListTraverser.traverse(varDef.pats)
    varDef.rhs.foreach { rhs =>
      write(" = ")
      rhsTermTraverser.traverse(rhs)
    }
  }
}
