package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.ModifiersRenderContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Mod.Annot

class ModifiersRenderContextMatcher(expectedContext: ModifiersRenderContext) extends ArgumentMatcher[ModifiersRenderContext] {

  override def matches(actualContext: ModifiersRenderContext): Boolean = {

    annotsMatch(actualContext) &&
      actualContext.annotsOnSameLine == expectedContext.annotsOnSameLine &&
      actualContext.hasImplicit == expectedContext.hasImplicit &&
      actualContext.javaModifiers == expectedContext.javaModifiers
  }

  private def annotsMatch(actualContext: ModifiersRenderContext) = {
    new ListMatcher(expectedContext.annots, new TreeMatcher[Annot](_)).matches(actualContext.annots)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object ModifiersRenderContextMatcher {
  def eqModifiersRenderContext(expectedContext: ModifiersRenderContext): ModifiersRenderContext =
    argThat(new ModifiersRenderContextMatcher(expectedContext))
}

