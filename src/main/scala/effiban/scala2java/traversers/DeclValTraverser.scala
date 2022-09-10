package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaModifiersResolverParams}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Decl

trait DeclValTraverser extends ScalaTreeTraverser[Decl.Val]

//TODO - if Java owner is an interface, the output should be an accessor method
private[traversers] class DeclValTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DeclValTraverser {

  import javaWriter._

  //TODO replace interface data member declaration (invalid in Java) with method declaration
  override def traverse(valDecl: Decl.Val): Unit = {
    annotListTraverser.traverseMods(valDecl.mods)
    writeModifiers(resolveJavaModifiers(valDecl))
    typeTraverser.traverse(valDecl.decltpe)
    write(" ")
    //TODO - verify when not simple case
    patListTraverser.traverse(valDecl.pats)
  }

  private def resolveJavaModifiers(valDecl: Decl.Val) = {
    val params = JavaModifiersResolverParams(
      scalaTree = valDecl,
      scalaMods = valDecl.mods,
      javaTreeType = JavaTreeType.Variable,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(params)
  }
}
