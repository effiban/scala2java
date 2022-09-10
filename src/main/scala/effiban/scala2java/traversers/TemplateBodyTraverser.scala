package effiban.scala2java.traversers

import effiban.scala2java.contexts.TemplateBodyContext
import effiban.scala2java.entities.CtorContext
import effiban.scala2java.orderings.JavaTemplateChildOrdering
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Stat, Term}

trait TemplateBodyTraverser {

  def traverse(statements: List[Stat], context: TemplateBodyContext = TemplateBodyContext()): Unit
}

private[traversers] class TemplateBodyTraverserImpl(templateChildTraverser: => TemplateChildTraverser,
                                                    javaTemplateChildOrdering: JavaTemplateChildOrdering)
                                                   (implicit javaWriter: JavaWriter) extends TemplateBodyTraverser {

  import javaWriter._

  def traverse(stats: List[Stat], context: TemplateBodyContext = TemplateBodyContext()): Unit = {
    val terms = stats.collect { case term: Term => term }
    val nonTerms = stats.filterNot(terms.contains(_))

    val children = context.maybePrimaryCtor match {
      case Some(primaryCtor) => nonTerms :+ primaryCtor
      case None => stats
    }
    val maybeCtorContext = context.maybeClassName.map(className => CtorContext(className = className, inits = context.inits, terms = terms))
    writeBlockStart()
    children.sorted(javaTemplateChildOrdering).foreach(child =>
      templateChildTraverser.traverse(child, maybeCtorContext)
    )
    writeBlockEnd()
  }
}
