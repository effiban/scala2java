package effiban.scala2java.matchers

import effiban.scala2java.contexts.TemplateContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.{Ctor, Type}

class TemplateContextMatcher(expectedTemplateContext: TemplateContext) extends ArgumentMatcher[TemplateContext] {

  override def matches(actualTemplateContext: TemplateContext): Boolean = {
    actualTemplateContext.javaScope == expectedTemplateContext.javaScope &&
      classNameMatches(actualTemplateContext) &&
      explicitPrimaryCtorMatches(actualTemplateContext) &&
      actualTemplateContext.javaPermittedSubTypeNames == expectedTemplateContext.javaPermittedSubTypeNames
  }

  private def classNameMatches(actualTemplateContext: TemplateContext) = {
    new OptionMatcher(expectedTemplateContext.maybeClassName, new TreeMatcher[Type.Name](_))
      .matches(actualTemplateContext.maybeClassName)
  }

  private def explicitPrimaryCtorMatches(actualTemplateContext: TemplateContext) = {
    new OptionMatcher(expectedTemplateContext.maybePrimaryCtor, new TreeMatcher[Ctor.Primary](_))
      .matches(actualTemplateContext.maybePrimaryCtor)
  }

  override def toString: String = s"Matcher for: $expectedTemplateContext"
}

object TemplateContextMatcher {
  def eqTemplateContext(expectedTemplateContext: TemplateContext): TemplateContext = argThat(new TemplateContextMatcher(expectedTemplateContext))
}

