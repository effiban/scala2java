package effiban.scala2java.traversers

import effiban.scala2java
import effiban.scala2java.TraversalConstants.UnknownType
import effiban.scala2java.TraversalContext.javaOwnerContext
import effiban.scala2java.{JavaEmitter, JavaModifiersResolver, Method}

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
      case modifiers if javaOwnerContext == scala2java.Class => javaModifiersResolver.resolveForClassDataMember(modifiers)
      case _ => Nil
    }
    emitModifiers(modifierNames)
    varDef.decltpe match {
      case Some(declType) =>
        typeTraverser.traverse(declType)
        emit(" ")
      case None if javaOwnerContext == Method => emit("var ")
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
