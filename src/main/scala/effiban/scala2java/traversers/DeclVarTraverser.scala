package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter
import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver

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
    val modifierNames = javaScope match {
      case JavaScope.Class => javaModifiersResolver.resolveForClassDataMember(varDecl.mods)
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
