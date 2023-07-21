package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateChildContext
import io.github.effiban.scala2java.core.orderings.JavaTemplateChildOrdering
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Tree

@deprecated
trait DeprecatedTemplateChildrenTraverser {

  def traverse(children: List[Tree], childContext: TemplateChildContext): Unit
}

@deprecated
private[traversers] class DeprecatedTemplateChildrenTraverserImpl(templateChildTraverser: => DeprecatedTemplateChildTraverser,
                                                                  javaTemplateChildOrdering: JavaTemplateChildOrdering)
                                                                 (implicit javaWriter: JavaWriter) extends DeprecatedTemplateChildrenTraverser {
  import javaWriter._

  def traverse(children: List[Tree], childContext: TemplateChildContext): Unit = {
    writeBlockStart()
    children.sorted(javaTemplateChildOrdering).foreach(child =>
      templateChildTraverser.traverse(child, childContext)
    )
    writeBlockEnd()
  }
}
