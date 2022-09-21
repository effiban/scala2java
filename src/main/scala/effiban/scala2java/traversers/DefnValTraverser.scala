package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait DefnValTraverser {
  def traverse(valDef: Defn.Val, context: StatContext = StatContext()): Unit
}

//TODO - if Java owner is an interface, the output should be an accessor method with default impl
private[traversers] class DefnValTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               defnValOrVarTypeTraverser: => DefnValOrVarTypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               rhsTermTraverser: => RhsTermTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DefnValTraverser {

  import javaWriter._

  //TODO if it is non-public it will be invalid in a Java interface - replace with method
  override def traverse(valDef: Defn.Val, context: StatContext = StatContext()): Unit = {
    annotListTraverser.traverseMods(valDef.mods)
    writeModifiers(resolveJavaModifiers(valDef, context.javaScope))
    defnValOrVarTypeTraverser.traverse(valDef.decltpe, Some(valDef.rhs), context)
    write(" ")
    //TODO verify for non-simple case
    patListTraverser.traverse(valDef.pats)
    write(" = ")
    rhsTermTraverser.traverse(valDef.rhs)
  }

  private def resolveJavaModifiers(valDef: Defn.Val, javaScope: JavaScope) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = valDef,
      scalaMods = valDef.mods,
      javaTreeType = JavaTreeType.Variable,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}
