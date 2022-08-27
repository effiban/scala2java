package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaModifiersResolverParams}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn
import scala.meta.Mod.Final

trait DefnValTraverser extends ScalaTreeTraverser[Defn.Val]

//TODO - if Java owner is an interface, the output should be an accessor method with default impl
private[traversers] class DefnValTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               defnValOrVarTypeTraverser: => DefnValOrVarTypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               termTraverser: => TermTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DefnValTraverser {

  import javaWriter._

  //TODO if it is non-public it will be invalid in a Java interface - replace with method
  def traverse(valDef: Defn.Val): Unit = {
    annotListTraverser.traverseMods(valDef.mods)
    writeModifiers(resolveJavaModifiers(valDef))
    defnValOrVarTypeTraverser.traverse(valDef.decltpe, Some(valDef.rhs))
    write(" ")
    //TODO verify for non-simple case
    patListTraverser.traverse(valDef.pats)
    write(" = ")
    termTraverser.traverse(valDef.rhs)
  }

  private def resolveJavaModifiers(valDef: Defn.Val) = {
    val params = JavaModifiersResolverParams(
      scalaTree = valDef,
      scalaMods = valDef.mods :+ Final(),
      javaTreeType = JavaTreeType.Variable,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(params)
  }
}
