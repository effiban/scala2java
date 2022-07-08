package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter
import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.JavaScope.{Interface, Method}
import effiban.scala2java.entities.TraversalConstants.UnknownType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver

import scala.meta.Defn
import scala.meta.Mod.Final

trait DefnValTraverser extends ScalaTreeTraverser[Defn.Val]

//TODO - if Java owner is an interface, the output should be an accessor method with default impl
private[scala2java] class DefnValTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               termTraverser: => TermTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaEmitter: JavaEmitter) extends DefnValTraverser {

  import javaEmitter._

  def traverse(valDef: Defn.Val): Unit = {
    annotListTraverser.traverseMods(valDef.mods)
    val mods = valDef.mods :+ Final()
    val modifierNames = mods match {
      case modifiers if javaScope == JavaScope.Class => javaModifiersResolver.resolveForClassDataMember(modifiers)
      case _ if javaScope == Interface => Nil
      // The only possible modifier for a method param or local var is 'final'
      case modifiers if javaScope == Method => javaModifiersResolver.resolve(modifiers, List(classOf[Final]))
      case _ => Nil
    }
    emitModifiers(modifierNames)
    valDef.decltpe match {
      case Some(declType) =>
        typeTraverser.traverse(declType)
        emit(" ")
      case None if javaScope == Method => emit("var ")
      case None =>
        emitComment(UnknownType)
        emit(" ")
      case _ =>
    }
    //TODO verify for non-simple case
    patListTraverser.traverse(valDef.pats)
    emit(" = ")
    termTraverser.traverse(valDef.rhs)
  }
}

object DefnValTraverser extends DefnValTraverserImpl(
  AnnotListTraverser,
  TypeTraverser,
  PatListTraverser,
  TermTraverser,
  JavaModifiersResolver
)

