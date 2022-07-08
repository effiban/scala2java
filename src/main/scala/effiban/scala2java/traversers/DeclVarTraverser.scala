package effiban.scala2java.traversers

import effiban.scala2java
import effiban.scala2java.TraversalContext.javaOwnerContext
import effiban.scala2java.{JavaEmitter, JavaModifiersResolver}

import scala.meta.Decl

trait DeclVarTraverser extends ScalaTreeTraverser[Decl.Var]

//TODO - if Java owner is an interface, the output should be a pair of accessor/mutator methods
private[scala2java] class DeclVarTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaEmitter: JavaEmitter) extends DeclVarTraverser {

  import javaEmitter._

  override def traverse(varDecl: Decl.Var): Unit = {
    annotListTraverser.traverseMods(varDecl.mods)
    val modifierNames = javaOwnerContext match {
      case scala2java.Class => javaModifiersResolver.resolveForClassDataMember(varDecl.mods)
      case _ => Nil
    }
    emitModifiers(modifierNames)
    typeTraverser.traverse(varDecl.decltpe)
    emit(" ")
    //TODO - verify when not simple case
    patListTraverser.traverse(varDecl.pats)
  }
}

object DeclVarTraverser extends DeclVarTraverserImpl(
  AnnotListTraverser,
  TypeTraverser,
  PatListTraverser,
  JavaModifiersResolver
)
