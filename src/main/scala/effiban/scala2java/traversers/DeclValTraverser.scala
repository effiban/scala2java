package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType.{Interface, Method}
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.Decl
import scala.meta.Mod.Final

trait DeclValTraverser extends ScalaTreeTraverser[Decl.Val]

//TODO - if Java owner is an interface, the output should be an accessor method
private[traversers] class DeclValTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DeclValTraverser {

  import javaWriter._

  override def traverse(valDecl: Decl.Val): Unit = {
    annotListTraverser.traverseMods(valDecl.mods)
    val mods = valDecl.mods :+ Final()
    val modifierNames = javaScope match {
      case JavaTreeType.Class => javaModifiersResolver.resolveForClassDataMember(mods)
      //TODO replace interface data member declaration (invalid in Java) with method declaration
      case _ if javaScope == Interface => Nil
      // The only possible Java modifier for a local var is 'final'
      case Method => List(JavaModifier.Final)
      case _ => Nil
    }
    writeModifiers(modifierNames)
    typeTraverser.traverse(valDecl.decltpe)
    write(" ")
    //TODO - verify when not simple case
    patListTraverser.traverse(valDecl.pats)
  }
}
