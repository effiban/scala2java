package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.JavaScope.{Interface, Method}
import effiban.scala2java.entities.TraversalConstants.UnknownType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn
import scala.meta.Mod.Final

trait DefnValTraverser extends ScalaTreeTraverser[Defn.Val]

//TODO - if Java owner is an interface, the output should be an accessor method with default impl
private[traversers] class DefnValTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               termTraverser: => TermTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DefnValTraverser {

  import javaWriter._

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
    writeModifiers(modifierNames)
    valDef.decltpe match {
      case Some(declType) =>
        typeTraverser.traverse(declType)
        write(" ")
      case None if javaScope == Method => write("var ")
      case None =>
        writeComment(UnknownType)
        write(" ")
      case _ =>
    }
    //TODO verify for non-simple case
    patListTraverser.traverse(valDef.pats)
    write(" = ")
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

