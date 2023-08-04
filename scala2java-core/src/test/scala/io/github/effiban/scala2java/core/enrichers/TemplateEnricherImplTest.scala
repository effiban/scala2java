package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.contexts.matchers.TemplateEnrichmentContextMockitoMatcher.eqTemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedTemplateScalatestMatcher.equalEnrichedTemplate
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDefnDef, EnrichedDefnVar, EnrichedMultiStat, EnrichedTemplate}
import io.github.effiban.scala2java.core.entities.JavaKeyword.Implements
import io.github.effiban.scala2java.core.resolvers.JavaInheritanceKeywordResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Name, Self, Template, Type, XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TemplateEnricherImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")
  private val TheJavaScope = JavaScope.Class

  private val TheContext = TemplateEnrichmentContext(
    javaScope = TheJavaScope,
    maybeClassName = Some(ClassName)
  )

  private val Init1 = init"Parent1()"
  private val Init2 = init"Parent2()"
  private val TheInits = List(Init1, Init2)

  private val TheSelf = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(t"SelfType"))

  private val TheDefnVar = q"var y = 4"
  private val TheEnrichedDefnVar = EnrichedDefnVar(TheDefnVar)

  private val TheDefnDef = q"def myMethod(param: Int): Int = doSomething(param)"
  private val TheEnrichedDefnDef = EnrichedDefnDef(TheDefnDef)

  private val TheStats = List(TheDefnVar, TheDefnDef)
  private val TheEnrichedStats = List(TheEnrichedDefnVar, TheEnrichedDefnDef)
  private val TheEnrichedBody = EnrichedMultiStat(TheEnrichedStats)

  private val javaInheritanceKeywordResolver = mock[JavaInheritanceKeywordResolver]
  private val templateBodyEnricher = mock[TemplateBodyEnricher]

  private val templateEnricher = new TemplateEnricherImpl(
    templateBodyEnricher,
    javaInheritanceKeywordResolver
  )

  test("enrich when has inits") {
    val template = Template(
      early = Nil,
      inits = TheInits,
      self = TheSelf,
      stats = TheStats
    )
    val expectedEnrichedTemplate = EnrichedTemplate(
      maybeInheritanceKeyword = Some(Implements),
      inits = TheInits,
      self = TheSelf,
      enrichedStats = TheEnrichedStats
    )

    when(javaInheritanceKeywordResolver.resolve(eqTo(TheJavaScope), eqTreeList(TheInits))).thenReturn(Implements)
    expectEnrichBody()

    templateEnricher.enrich(template, TheContext) should equalEnrichedTemplate(expectedEnrichedTemplate)
  }

  test("enrich when has no inits") {
    val template = Template(
      early = Nil,
      inits = Nil,
      self = TheSelf,
      stats = TheStats
    )
    val expectedEnrichedTemplate = EnrichedTemplate(
      inits = Nil,
      self = TheSelf,
      enrichedStats = TheEnrichedStats
    )

    expectEnrichBody()

    templateEnricher.enrich(template, TheContext) should equalEnrichedTemplate(expectedEnrichedTemplate)
  }

  private def expectEnrichBody(): Unit = {
    doReturn(TheEnrichedBody)
      .when(templateBodyEnricher).enrich(eqTreeList(TheStats), eqTemplateEnrichmentContext(TheContext))
  }
}
