package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.contexts.matchers.TemplateEnrichmentContextMockitoMatcher.eqTemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedMultiStatScalatestMatcher.equalEnrichedMultiStat
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDefnDef, EnrichedDefnVar, EnrichedMultiStat, EnrichedSimpleStat}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Stat, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TemplateBodyEnricherImplTest extends UnitTestSuite {

  private val TheContext = TemplateEnrichmentContext(JavaScope.Class, Some(t"MyClass"))

  private val Statement1 = q"final var x: Int = 3"
  private val Statement2 = q"def foo(x: Int) = x + 1"
  private val Statements = List(Statement1, Statement2)

  private val EnrichedStatement1 = EnrichedDefnVar(Statement1)
  private val EnrichedStatement2 = EnrichedDefnDef(Statement2)
  private val EnrichedStatements = List(EnrichedStatement1, EnrichedStatement2)

  private val EnrichedTemplateBody = EnrichedMultiStat(EnrichedStatements)

  private val templateStatEnricher = mock[TemplateStatEnricher]

  private val templateBodyEnricher = new TemplateBodyEnricherImpl(templateStatEnricher)

  test("enrich") {
    doAnswer((stat: Stat, _: TemplateEnrichmentContext) => stat match {
      case aStat if aStat.structure == Statement1.structure => EnrichedStatement1
      case aStat if aStat.structure == Statement2.structure => EnrichedStatement2
      case aStat => EnrichedSimpleStat(aStat)
    }).when(templateStatEnricher).enrich(any[Stat], eqTemplateEnrichmentContext(TheContext))

    templateBodyEnricher.enrich(Statements, TheContext) should equalEnrichedMultiStat(EnrichedTemplateBody)
  }
}
