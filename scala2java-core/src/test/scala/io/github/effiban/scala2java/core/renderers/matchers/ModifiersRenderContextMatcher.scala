package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.ModifiersRenderContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Mod

class ModifiersRenderContextMatcher(expectedContext: ModifiersRenderContext) extends ArgumentMatcher[ModifiersRenderContext] {

  override def matches(actualContext: ModifiersRenderContext): Boolean = {

    scalaModsMatch(actualContext) &&
      actualContext.annotsOnSameLine == expectedContext.annotsOnSameLine &&
      actualContext.javaModifiers == expectedContext.javaModifiers
  }

  private def scalaModsMatch(actualContext: ModifiersRenderContext) = {
    new ListMatcher(expectedContext.scalaMods, new TreeMatcher[Mod](_)).matches(actualContext.scalaMods)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object ModifiersRenderContextMatcher {
  def eqModifiersRenderContext(expectedContext: ModifiersRenderContext): ModifiersRenderContext =
    argThat(new ModifiersRenderContextMatcher(expectedContext))
}

