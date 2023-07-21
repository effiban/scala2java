package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts._
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class DefnRenderContextMatcher(expectedContext: DefnRenderContext) extends ArgumentMatcher[DefnRenderContext] {

  override def matches(actualContext: DefnRenderContext): Boolean = {
    (actualContext, expectedContext) match {
      case (actualTraitContext: TraitRenderContext, expectedTraitContext: TraitRenderContext) =>
        new TraitRenderContextMatcher(expectedTraitContext).matches(actualTraitContext)
      case (actualCaseClassContext: CaseClassRenderContext, expectedCaseClassContext: CaseClassRenderContext) =>
        new CaseClassRenderContextMatcher(expectedCaseClassContext).matches(actualCaseClassContext)
      case (actualRegularClassContext: RegularClassRenderContext, expectedRegularClassContext: RegularClassRenderContext) =>
        new RegularClassRenderContextMatcher(expectedRegularClassContext).matches(actualRegularClassContext)
      case (actualObjectContext: ObjectRenderContext, expectedObjectContext: ObjectRenderContext) =>
        new ObjectRenderContextMatcher(expectedObjectContext).matches(actualObjectContext)
      case (anActualContext, anExpectedContext) => anActualContext == anExpectedContext
    }
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object DefnRenderContextMatcher {
  def eqDefnRenderContext(expectedContext: DefnRenderContext): DefnRenderContext =
    argThat(new DefnRenderContextMatcher(expectedContext))
}

