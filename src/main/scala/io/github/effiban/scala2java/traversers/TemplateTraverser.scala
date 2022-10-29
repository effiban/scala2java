package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{InitContext, TemplateBodyContext, TemplateContext}
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.predicates.TemplateInitIncludedPredicate
import io.github.effiban.scala2java.resolvers.JavaInheritanceKeywordResolver
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.{Init, Template}

trait TemplateTraverser {

  def traverse(template: Template, context: TemplateContext): Unit
}

private[traversers] class TemplateTraverserImpl(initListTraverser: => InitListTraverser,
                                                selfTraverser: => SelfTraverser,
                                                templateBodyTraverser: => TemplateBodyTraverser,
                                                permittedSubTypeNameListTraverser: PermittedSubTypeNameListTraverser,
                                                javaInheritanceKeywordResolver: JavaInheritanceKeywordResolver,
                                                templateInitIncludedPredicate: TemplateInitIncludedPredicate)
                                               (implicit javaWriter: JavaWriter) extends TemplateTraverser {

  import javaWriter._

  def traverse(template: Template, context: TemplateContext): Unit = {
    val includedInits = template.inits.filter(templateInitIncludedPredicate)
    traverseTemplateInits(includedInits, context.javaScope)
    selfTraverser.traverse(template.self)
    if (context.permittedSubTypeNames.nonEmpty) {
      write(" ")
      permittedSubTypeNameListTraverser.traverse(context.permittedSubTypeNames)
    }
    val bodyContext = TemplateBodyContext(
      javaScope = context.javaScope,
      maybeClassName = context.maybeClassName,
      maybePrimaryCtor = context.maybePrimaryCtor,
      inits = includedInits
    )
    templateBodyTraverser.traverse(statements = template.stats, context = bodyContext)
  }

  private def traverseTemplateInits(inits: List[Init], javaScope: JavaScope): Unit = {
    if (inits.nonEmpty) {
      val inheritanceKeyword = javaInheritanceKeywordResolver.resolve(javaScope, inits)
      write(" ")
      writeKeyword(inheritanceKeyword)
      write(" ")
      initListTraverser.traverse(inits, InitContext(ignoreArgs = true))
    }
  }
}
