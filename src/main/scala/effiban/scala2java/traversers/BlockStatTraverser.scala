package effiban.scala2java.traversers

import effiban.scala2java.classifiers.JavaStatClassifier
import effiban.scala2java.entities.Decision.{Decision, No, Uncertain, Yes}
import effiban.scala2java.entities.TraversalConstants.UncertainReturn
import effiban.scala2java.resolvers.ShouldReturnValueResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.{If, Return}
import scala.meta.{Stat, Term}

trait BlockStatTraverser {

  def traverse(stat: Stat): Unit

  def traverseLast(stat: Stat, shouldReturnValue: Decision = No): Unit
}

private[traversers] class BlockStatTraverserImpl(ifTraverser: => IfTraverser,
                                                 statTraverser: => StatTraverser,
                                                 shouldReturnValueResolver: => ShouldReturnValueResolver,
                                                 javaStatClassifier: JavaStatClassifier)
                                                (implicit javaWriter: JavaWriter) extends BlockStatTraverser {

  import javaWriter._

  override def traverse(stat: Stat): Unit = {
    statTraverser.traverse(stat)
    writeStatEnd(stat)
  }

  override def traverseLast(stat: Stat, shouldReturnValue: Decision = No): Unit = {
    stat match {
      case `if`: If => ifTraverser.traverse(`if`, shouldReturnValue)
      case term: Term => traverseLastTerm(term, shouldReturnValue)
      case _ => traverse(stat)
    }
  }

  private def traverseLastTerm(term: Term, shouldReturnValue: Decision): Unit = {
    val shouldTermReturnValue = shouldReturnValueResolver.resolve(term, shouldReturnValue)
    shouldTermReturnValue match {
      case Yes => traverse(Return(term))
      case Uncertain =>
        writeComment(UncertainReturn)
        traverse(term)
      case No => traverse(term)
    }
  }

  private def writeStatEnd(stat: Stat): Unit = {
    if (javaStatClassifier.requiresEndDelimiter(stat)) {
      writeStatementEnd()
    }
  }
}
