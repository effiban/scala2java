package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateChildContext
import io.github.effiban.scala2java.core.orderings.JavaTemplateChildOrdering
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Tree

trait TemplateChildrenTraverser {

  def traverse(children: List[Tree], childContext: TemplateChildContext): Unit
}

private[traversers] class TemplateChildrenTraverserImpl(templateChildTraverser: => TemplateChildTraverser,
                                                        javaTemplateChildOrdering: JavaTemplateChildOrdering)
                                                       (implicit javaWriter: JavaWriter) extends TemplateChildrenTraverser {
  import javaWriter._

  def traverse(children: List[Tree], childContext: TemplateChildContext): Unit = {
    writeBlockStart()
    children.sorted(javaTemplateChildOrdering).foreach(child =>
      templateChildTraverser.traverse(child, childContext)
    )
    writeBlockEnd()
  }
}
