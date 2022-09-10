package effiban.scala2java.traversers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.Decl

trait DeclVarTraverser extends ScalaTreeTraverser[Decl.Var]

//TODO - if Java owner is an interface, the output should be a pair of accessor/mutator methods
private[traversers] class DeclVarTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DeclVarTraverser {

  import javaWriter._

  //TODO replace mutable interface data member (invalid in Java) with method
  override def traverse(varDecl: Decl.Var): Unit = {
    annotListTraverser.traverseMods(varDecl.mods)
    writeModifiers(resolveJavaModifiers(varDecl))
    typeTraverser.traverse(varDecl.decltpe)
    write(" ")
    //TODO - verify when not simple case
    patListTraverser.traverse(varDecl.pats)
  }

  private def resolveJavaModifiers(varDecl: Decl.Var) = {
    val context = JavaModifiersContext(
      scalaTree = varDecl,
      scalaMods = varDecl.mods,
      javaTreeType = JavaTreeType.Variable,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(context)
  }
}
