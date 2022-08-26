package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaModifier
import effiban.scala2java.entities.JavaScope.Lambda
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermParamTraverser extends ScalaTreeTraverser[Term.Param]

private[traversers] class TermParamTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                 typeTraverser: => TypeTraverser,
                                                 nameTraverser: => NameTraverser)
                                                (implicit javaWriter: JavaWriter) extends TermParamTraverser {

  import javaWriter._

  // method parameter declaration
  override def traverse(termParam: Term.Param): Unit = {
    annotListTraverser.traverseMods(termParam.mods, onSameLine = true)
    val javaModifiers: List[JavaModifier] = javaScope match {
      case Lambda => Nil
      case _ => List(JavaModifier.Final)
    }
    writeModifiers(javaModifiers)
    termParam.decltpe.foreach(declType => {
      typeTraverser.traverse(declType)
      write(" ")
    })
    nameTraverser.traverse(termParam.name)
    // TODO handle 'default'
  }
}
