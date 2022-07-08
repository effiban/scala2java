package effiban.scala2java.traversers

import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{Interface, Method}
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.{JavaEmitter, entities}

import scala.meta.Decl
import scala.meta.Mod.Final

trait DeclValTraverser extends ScalaTreeTraverser[Decl.Val]

//TODO - if Java owner is an interface, the output should be an accessor method
private[scala2java] class DeclValTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaEmitter: JavaEmitter) extends DeclValTraverser {

  import javaEmitter._

  override def traverse(valDecl: Decl.Val): Unit = {
    annotListTraverser.traverseMods(valDecl.mods)
    val mods = valDecl.mods :+ Final()
    val modifierNames = javaScope match {
      case entities.Class => javaModifiersResolver.resolveForClassDataMember(mods)
      //TODO replace interface data member (invalid in Java) with method
      case _ if javaScope == Interface => Nil
      // The only possible modifier for a local var is 'final'
      case Method => javaModifiersResolver.resolve(mods, List(classOf[Final]))
      case _ => Nil
    }
    emitModifiers(modifierNames)
    typeTraverser.traverse(valDecl.decltpe)
    emit(" ")
    //TODO - verify when not simple case
    patListTraverser.traverse(valDecl.pats)
  }
}

object DeclValTraverser extends DeclValTraverserImpl(
  AnnotListTraverser,
  TypeTraverser,
  PatListTraverser,
  JavaModifiersResolver
)
