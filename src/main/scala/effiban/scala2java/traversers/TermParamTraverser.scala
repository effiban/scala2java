package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaScope.Lambda
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.Mod.Final
import scala.meta.Term

trait TermParamTraverser extends ScalaTreeTraverser[Term.Param]

private[traversers] class TermParamTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                 typeTraverser: => TypeTraverser,
                                                 nameTraverser: => NameTraverser,
                                                 javaModifiersResolver: JavaModifiersResolver)
                                                (implicit javaWriter: JavaWriter) extends TermParamTraverser {

  import javaWriter._

  // method parameter declaration
  override def traverse(termParam: Term.Param): Unit = {
    annotListTraverser.traverseMods(termParam.mods, onSameLine = true)
    val mods = javaScope match {
      case Lambda => termParam.mods
      case _ => termParam.mods :+ Final()
    }
    val modifierNames = javaModifiersResolver.resolve(mods, List("final"))
    writeModifiers(modifierNames)
    termParam.decltpe.foreach(declType => {
      typeTraverser.traverse(declType)
      write(" ")
    })
    nameTraverser.traverse(termParam.name)
    // TODO handle 'default'
  }
}
