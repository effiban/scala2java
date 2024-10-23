package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import io.github.effiban.scala2java.test.utils.matchers.{TreeKeyedMapMockitoMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Type

class QualificationContextMockitoMatcher(expectedContext: QualificationContext) extends ArgumentMatcher[QualificationContext] {

  override def matches(actualContext: QualificationContext): Boolean = {
    importersMatch(actualContext) && qualifiedTypeMapsMatch(actualContext)
  }

  private def importersMatch(actualContext: QualificationContext) = {
    actualContext.importers.structure == expectedContext.importers.structure
  }

  private def qualifiedTypeMapsMatch(actualContext: QualificationContext) = {
    new TreeKeyedMapMockitoMatcher(expectedContext.qualifiedTypeMap, new TreeMatcher[Type](_)).matches(actualContext.qualifiedTypeMap)
  }
}

object QualificationContextMockitoMatcher {
  def eqQualificationContext(expectedContext: QualificationContext): QualificationContext = argThat(new QualificationContextMockitoMatcher(expectedContext))
}

