package io.github.effiban.scala2java.core.matchers

import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Template

class TemplateMockitoMatcher(expectedTemplate: Template) extends ArgumentMatcher[Template] {

  override def matches(actualTemplate: Template): Boolean = {
    // TODO handle 'early'
    initsMatch(actualTemplate) &&
      selfMatches(actualTemplate) &&
      statsMatch(actualTemplate)
  }

  private def initsMatch(actualTemplate: Template) = {
    actualTemplate.inits.structure == expectedTemplate.inits.structure
  }

  private def selfMatches(actualTemplate: Template) = {
    actualTemplate.self.structure == expectedTemplate.self.structure
  }

  private def statsMatch(actualTemplate: Template) = {
    actualTemplate.stats.structure == expectedTemplate.stats.structure
  }
}

object TemplateMockitoMatcher {

  def eqTemplate(expectedTemplate: Template): Template =
    argThat(new TemplateMockitoMatcher(expectedTemplate))
}

