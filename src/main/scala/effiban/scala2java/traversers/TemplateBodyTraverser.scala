package effiban.scala2java.traversers

import effiban.scala2java.classifiers.DefnTypeClassifier
import effiban.scala2java.contexts.{CtorContext, TemplateBodyContext, TemplateChildContext}
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.orderings.JavaTemplateChildOrdering
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Defn, Stat, Term, Tree}

trait TemplateBodyTraverser {

  def traverse(statements: List[Stat], context: TemplateBodyContext): Unit
}

private[traversers] class TemplateBodyTraverserImpl(templateChildTraverser: => TemplateChildTraverser,
                                                    defnTypeClassifier: DefnTypeClassifier,
                                                    javaTemplateChildOrdering: JavaTemplateChildOrdering)
                                                   (implicit javaWriter: JavaWriter) extends TemplateBodyTraverser {

  import javaWriter._

  def traverse(stats: List[Stat], context: TemplateBodyContext): Unit = {
    val terms = stats.collect { case term: Term => term }
    val nonTerms = stats.filterNot(terms.contains(_))

    val children = (context.maybePrimaryCtor match {
      case Some(primaryCtor) => nonTerms :+ primaryCtor
      case None => stats
    }).filterNot(isEnumTypeDef)

    val maybeCtorContext = context.maybeClassName.map(className => CtorContext(className = className, inits = context.inits, terms = terms))

    writeBlockStart()
    children.sorted(javaTemplateChildOrdering).foreach(child =>
      templateChildTraverser.traverse(child, TemplateChildContext(javaScope = context.javaScope, maybeCtorContext = maybeCtorContext))
    )
    writeBlockEnd()
  }

  private def isEnumTypeDef(tree: Tree): Boolean = tree match {
    // The particular Scala typedef required for an enumeration is redundant in Java
    case defnType: Defn.Type if defnTypeClassifier.isEnumTypeDef(defnType, javaScope) => true
    case _ => false
  }
}
