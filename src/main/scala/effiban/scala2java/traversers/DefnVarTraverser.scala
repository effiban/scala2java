package effiban.scala2java.traversers

import effiban.scala2java.entities.Method
import effiban.scala2java.entities.TraversalConstants.UnknownType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.{JavaEmitter, entities}

import scala.meta.Defn

trait DefnVarTraverser extends ScalaTreeTraverser[Defn.Var]

//TODO - if Java owner is an interface, the output should be a pair of accessor/mutator methods with default impls
private[scala2java] class DefnVarTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               termTraverser: => TermTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaEmitter: JavaEmitter) extends DefnVarTraverser {

  import javaEmitter._

  override def traverse(varDef: Defn.Var): Unit = {
    annotListTraverser.traverseMods(varDef.mods)
    val modifierNames = varDef.mods match {
      case modifiers if javaScope == entities.Class => javaModifiersResolver.resolveForClassDataMember(modifiers)
      case _ => Nil
    }
    emitModifiers(modifierNames)
    varDef.decltpe match {
      case Some(declType) =>
        typeTraverser.traverse(declType)
        emit(" ")
      case None if javaScope == Method => emit("var ")
      case None =>
        emitComment(UnknownType)
        emit(" ")
      case _ =>
    }
    //TODO - verify this
    patListTraverser.traverse(varDef.pats)
    varDef.rhs.foreach { rhs =>
      emit(" = ")
      termTraverser.traverse(rhs)
    }
  }
}

object DefnVarTraverser extends DefnVarTraverserImpl(AnnotListTraverser,
  TypeTraverser,
  PatListTraverser,
  TermTraverser,
  JavaModifiersResolver
)
