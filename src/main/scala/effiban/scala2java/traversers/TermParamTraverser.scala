package effiban.scala2java.traversers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermParamTraverser extends ScalaTreeTraverser[Term.Param]

private[traversers] class TermParamTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                 typeTraverser: => TypeTraverser,
                                                 nameTraverser: => NameTraverser,
                                                 javaModifiersResolver: JavaModifiersResolver)
                                                (implicit javaWriter: JavaWriter) extends TermParamTraverser {

  import javaWriter._

  // method/lambda parameter declaration
  // Note that a primary ctor. param in Scala is also a class member which requires additional handling,
  // but that aspect will be handled by one of the parent traversers before this one is called
  override def traverse(termParam: Term.Param): Unit = {
    annotListTraverser.traverseMods(termParam.mods, onSameLine = true)
    writeModifiers(resolveJavaModifiers(termParam))
    termParam.decltpe.foreach(declType => {
      typeTraverser.traverse(declType)
      write(" ")
    })
    nameTraverser.traverse(termParam.name)
    // TODO handle 'default'
  }

  private def resolveJavaModifiers(termParam: Term.Param) = {
    val context = JavaModifiersContext(
      scalaTree = termParam,
      scalaMods = termParam.mods,
      javaTreeType = JavaTreeType.Parameter,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(context)
  }
}
