package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{InitContext, TemplateBodyContext, TemplateContext}
import io.github.effiban.scala2java.core.resolvers.JavaInheritanceKeywordResolver
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.predicates.TemplateInitExcludedPredicate

import scala.meta.{Init, Template}

trait TemplateTraverser {

  def traverse(template: Template, context: TemplateContext): Unit
}

private[traversers] class TemplateTraverserImpl(initListTraverser: => DeprecatedInitListTraverser,
                                                selfTraverser: => SelfTraverser,
                                                templateBodyTraverser: => TemplateBodyTraverser,
                                                permittedSubTypeNameListTraverser: PermittedSubTypeNameListTraverser,
                                                javaInheritanceKeywordResolver: JavaInheritanceKeywordResolver,
                                                templateInitExcludedPredicate: TemplateInitExcludedPredicate)
                                               (implicit javaWriter: JavaWriter) extends TemplateTraverser {

  import javaWriter._

  def traverse(template: Template, context: TemplateContext): Unit = {
    val includedInits = template.inits.filterNot(templateInitExcludedPredicate)
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
