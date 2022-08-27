package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaModifiersResolverParams}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait DefnVarTraverser extends ScalaTreeTraverser[Defn.Var]

//TODO - if Java owner is an interface, the output should be a pair of accessor/mutator methods with default impls
private[traversers] class DefnVarTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               defnValOrVarTypeTraverser: => DefnValOrVarTypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               termTraverser: => TermTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DefnVarTraverser {

  import javaWriter._

  //TODO replace mutable interface data member (invalid in Java) with method
  override def traverse(varDef: Defn.Var): Unit = {
    annotListTraverser.traverseMods(varDef.mods)
    writeModifiers(resolveJavaModifiers(varDef))
    defnValOrVarTypeTraverser.traverse(varDef.decltpe, varDef.rhs)
    write(" ")
    //TODO - verify this
    patListTraverser.traverse(varDef.pats)
    varDef.rhs.foreach { rhs =>
      write(" = ")
      termTraverser.traverse(rhs)
    }
  }

  private def resolveJavaModifiers(varDef: Defn.Var) = {
    val params = JavaModifiersResolverParams(
      scalaTree = varDef,
      scalaMods = varDef.mods,
      javaTreeType = JavaTreeType.Variable,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(params)
  }
}
