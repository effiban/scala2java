package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class QualificationContextMockitoMatcher(expectedContext: QualificationContext) extends ArgumentMatcher[QualificationContext] {

  override def matches(actualContext: QualificationContext): Boolean = {
    importersMatch(actualContext)
  }

  private def importersMatch(actualContext: QualificationContext) = {
    actualContext.importers.structure == expectedContext.importers.structure
  }
}

object QualificationContextMockitoMatcher {
  def eqQualificationContext(expectedContext: QualificationContext): QualificationContext = argThat(new QualificationContextMockitoMatcher(expectedContext))
}

