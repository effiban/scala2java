package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.renderers.PatListRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn

@deprecated
trait DeprecatedDefnVarTraverser {
  def traverse(varDef: Defn.Var, context: StatContext = StatContext()): Unit
}

@deprecated
private[traversers] class DeprecatedDefnVarTraverserImpl(modListTraverser: => DeprecatedModListTraverser,
                                                         defnValOrVarTypeTraverser: => DeprecatedDefnValOrVarTypeTraverser,
                                                         patTraverser: => PatTraverser,
                                                         patListRenderer: => PatListRenderer,
                                                         expressionTermTraverser: => DeprecatedExpressionTermTraverser)
                                                        (implicit javaWriter: JavaWriter) extends DeprecatedDefnVarTraverser {

  import javaWriter._

  //TODO replace mutable interface data member (invalid in Java) with accessor/mutator methods
  override def traverse(varDef: Defn.Var, context: StatContext = StatContext()): Unit = {
    modListTraverser.traverse(ModifiersContext(varDef, JavaTreeType.Variable, context.javaScope))
    defnValOrVarTypeTraverser.traverse(varDef.decltpe, varDef.rhs, context)
    write(" ")
    //TODO - verify this
    val traversedPats = varDef.pats.map(patTraverser.traverse)
    patListRenderer.render(traversedPats)
    varDef.rhs.foreach { rhs =>
      write(" = ")
      expressionTermTraverser.traverse(rhs)
    }
  }
}
