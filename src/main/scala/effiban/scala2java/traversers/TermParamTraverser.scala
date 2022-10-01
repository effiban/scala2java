package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, StatContext}
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermParamTraverser {
  def traverse(termParam: Term.Param, context: StatContext): Unit
}

private[traversers] class TermParamTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                 typeTraverser: => TypeTraverser,
                                                 nameTraverser: => NameTraverser,
                                                 javaModifiersResolver: JavaModifiersResolver)
                                                (implicit javaWriter: JavaWriter) extends TermParamTraverser {

  import javaWriter._

  // method/lambda parameter declaration
  // Note that a primary ctor. param in Scala is also a class member which requires additional handling,
  // but that aspect will be handled by one of the parent traversers before this one is called
  override def traverse(termParam: Term.Param, context: StatContext): Unit = {
    annotListTraverser.traverseMods(termParam.mods, onSameLine = true)
    writeModifiers(resolveJavaModifiers(termParam, context.javaScope))
    termParam.decltpe.foreach(declType => {
      typeTraverser.traverse(declType)
      write(" ")
    })
    nameTraverser.traverse(termParam.name)
    termParam.default.foreach(default => writeComment(s"= ${default.toString()}"))
  }

  private def resolveJavaModifiers(termParam: Term.Param, parentJavaScope: JavaScope) = {
    val context = JavaModifiersContext(
      scalaTree = termParam,
      scalaMods = termParam.mods,
      javaTreeType = JavaTreeType.Parameter,
      javaScope = parentJavaScope
    )
    javaModifiersResolver.resolve(context)
  }
}
