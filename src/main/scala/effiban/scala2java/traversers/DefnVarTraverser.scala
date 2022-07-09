package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.JavaScope.Method
import effiban.scala2java.entities.TraversalConstants.UnknownType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait DefnVarTraverser extends ScalaTreeTraverser[Defn.Var]

//TODO - if Java owner is an interface, the output should be a pair of accessor/mutator methods with default impls
private[traversers] class DefnVarTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               termTraverser: => TermTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DefnVarTraverser {

  import javaWriter._

  override def traverse(varDef: Defn.Var): Unit = {
    annotListTraverser.traverseMods(varDef.mods)
    val modifierNames = varDef.mods match {
      case modifiers if javaScope == JavaScope.Class => javaModifiersResolver.resolveForClassDataMember(modifiers)
      case _ => Nil
    }
    writeModifiers(modifierNames)
    varDef.decltpe match {
      case Some(declType) =>
        typeTraverser.traverse(declType)
        write(" ")
      case None if javaScope == Method => write("var ")
      case None =>
        writeComment(UnknownType)
        write(" ")
      case _ =>
    }
    //TODO - verify this
    patListTraverser.traverse(varDef.pats)
    varDef.rhs.foreach { rhs =>
      write(" = ")
      termTraverser.traverse(rhs)
    }
  }
}
