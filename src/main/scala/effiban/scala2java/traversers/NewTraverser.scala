package effiban.scala2java.traversers

import effiban.scala2java.contexts.InitContext
import effiban.scala2java.entities.JavaKeyword
import effiban.scala2java.resolvers.ArrayInitializerContextResolver
import effiban.scala2java.writers.JavaWriter

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
