package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Importer

class QualificationContextMockitoMatcher(expectedContext: QualificationContext) extends ArgumentMatcher[QualificationContext] {

  override def matches(actualContext: QualificationContext): Boolean = {
    importersMatch(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def importersMatch(actualContext: QualificationContext) = {
    new ListMatcher(expectedContext.importers, new TreeMatcher[Importer](_)).matches(actualContext.importers)
  }
}

object QualificationContextMockitoMatcher {
  def eqQualificationContext(expectedContext: QualificationContext): QualificationContext = argThat(new QualificationContextMockitoMatcher(expectedContext))
}

