package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.InitContext
import io.github.effiban.scala2java.core.entities.JavaKeyword
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.New

trait NewTraverser extends ScalaTreeTraverser[New]

private[traversers] class NewTraverserImpl(initTraverser: => InitTraverser,
                                           arrayInitializerTraverser: => ArrayInitializerTraverser,
                                           arrayInitializerContextResolver: => ArrayInitializerContextResolver)
                                          (implicit javaWriter: JavaWriter) extends NewTraverser {

  import javaWriter._

  override def traverse(`new`: New): Unit = {
    arrayInitializerContextResolver.tryResolve(`new`.init) match {
      case Some(context) => arrayInitializerTraverser.traverseWithSize(context)
      case None => traverseRegular(`new`)
    }
  }

  private def traverseRegular(`new`: New): Unit = {
    writeKeyword(JavaKeyword.New)
    write(" ")
    initTraverser.traverse(`new`.init, InitContext(traverseEmpty = true, argNameAsComment = true))
  }
}
