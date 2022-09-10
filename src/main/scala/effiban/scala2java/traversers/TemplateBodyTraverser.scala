package effiban.scala2java.traversers

import effiban.scala2java.contexts.TemplateContext
import effiban.scala2java.entities.CtorContext
import effiban.scala2java.orderings.JavaTemplateChildOrdering
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Init, Stat, Term}

trait TemplateBodyTraverser {

  def traverse(statements: List[Stat],
               inits: List[Init],
               context: TemplateContext = TemplateContext()): Unit
}

private[traversers] class TemplateBodyTraverserImpl(templateChildTraverser: => TemplateChildTraverser,
                                                    javaTemplateChildOrdering: JavaTemplateChildOrdering)
                                                   (implicit javaWriter: JavaWriter) extends TemplateBodyTraverser {

  import javaWriter._

  def traverse(stats: List[Stat],
               inits: List[Init],
               context: TemplateContext = TemplateContext()): Unit = {
    val terms = stats.collect { case term: Term => term }
    val nonTerms = stats.filterNot(terms.contains(_))

    val children = context.maybePrimaryCtor match {
      case Some(primaryCtor) => nonTerms :+ primaryCtor
      case None => stats
    }
    val maybeCtorContext = context.maybeClassName.map(className => CtorContext(className = className, inits = inits, terms = terms))
    writeBlockStart()
    children.sorted(javaTemplateChildOrdering).foreach(child =>
      templateChildTraverser.traverse(child, maybeCtorContext)
    )
    writeBlockEnd()
  }
}
