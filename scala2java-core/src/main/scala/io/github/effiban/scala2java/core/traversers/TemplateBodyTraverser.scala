package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.DefnTypeClassifier
import io.github.effiban.scala2java.core.contexts.{TemplateBodyContext, TemplateChildContext}
import io.github.effiban.scala2java.core.orderings.JavaTemplateChildOrdering
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

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

    val ctorTerms = context.maybePrimaryCtor match {
      case Some(_) => terms
      case None => Nil
    }

    val children = (context.maybePrimaryCtor match {
      case Some(primaryCtor) => nonTerms :+ primaryCtor
      case None => stats
    }).filterNot(child => isEnumTypeDef(child, context.javaScope))

    val childContext = TemplateChildContext(
      javaScope = context.javaScope,
      maybeClassName = context.maybeClassName,
      inits = context.inits,
      ctorTerms = ctorTerms
    )

    writeBlockStart()
    children.sorted(javaTemplateChildOrdering).foreach(child =>
      templateChildTraverser.traverse(child, childContext)
    )
    writeBlockEnd()
  }

  private def isEnumTypeDef(tree: Tree, javaScope: JavaScope): Boolean = tree match {
    // The particular Scala typedef required for an enumeration is redundant in Java
    case defnType: Defn.Type if defnTypeClassifier.isEnumTypeDef(defnType, javaScope) => true
    case _ => false
  }
}
