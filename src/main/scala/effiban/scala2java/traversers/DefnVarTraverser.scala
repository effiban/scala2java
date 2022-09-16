package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait DefnVarTraverser {
  def traverse(varDef: Defn.Var, context: StatContext = StatContext()): Unit
}

//TODO - if Java owner is an interface, the output should be a pair of accessor/mutator methods with default impls
private[traversers] class DefnVarTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               defnValOrVarTypeTraverser: => DefnValOrVarTypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               rhsTermTraverser: => RhsTermTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DefnVarTraverser {

  import javaWriter._

  //TODO replace mutable interface data member (invalid in Java) with method
  override def traverse(varDef: Defn.Var, context: StatContext = StatContext()): Unit = {
    annotListTraverser.traverseMods(varDef.mods)
    writeModifiers(resolveJavaModifiers(varDef))
    defnValOrVarTypeTraverser.traverse(varDef.decltpe, varDef.rhs, context)
    write(" ")
    //TODO - verify this
    patListTraverser.traverse(varDef.pats)
    varDef.rhs.foreach { rhs =>
      write(" = ")
      rhsTermTraverser.traverse(rhs)
    }
  }

  private def resolveJavaModifiers(varDef: Defn.Var) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = varDef,
      scalaMods = varDef.mods,
      javaTreeType = JavaTreeType.Variable,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}
