package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedStat, EnrichedTemplate}
import io.github.effiban.scala2java.core.renderers.contexts.{TemplateBodyRenderContext, TemplateStatRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.TemplateBodyRenderContextScalatestMatcher.equalTemplateBodyRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.XtensionQuasiquoteTerm

class TemplateBodyRenderContextFactoryImplTest extends UnitTestSuite {

  private val templateStatRenderContextFactory = mock[TemplateStatRenderContextFactory]

  private val templateBodyRenderContextFactory = new TemplateBodyRenderContextFactoryImpl(templateStatRenderContextFactory)


  test("apply to EnrichedTemplate") {
    val stat1 = q"foo(1)"
    val stat2 = q"foo(2)"

    val enrichedStat1 = mock[EnrichedStat]
    val enrichedStat2 = mock[EnrichedStat]

    val statRenderContext1 = mock[TemplateStatRenderContext]
    val statRenderContext2 = mock[TemplateStatRenderContext]

    val enrichedTemplate = mock[EnrichedTemplate]

    when(enrichedTemplate.enrichedStats).thenReturn(List(enrichedStat1, enrichedStat2))
    when(enrichedStat1.stat).thenReturn(stat1)
    when(enrichedStat2.stat).thenReturn(stat2)

    val expectedBodyRenderContext = TemplateBodyRenderContext(
      Map(stat1 -> statRenderContext1, stat2 -> statRenderContext2)
    )

    doAnswer((enrichedStat: EnrichedStat) => enrichedStat match {
      case aStatResult if aStatResult == enrichedStat1 => statRenderContext1
      case aStatResult if aStatResult == enrichedStat2 => statRenderContext2
    }).when(templateStatRenderContextFactory)(any[EnrichedStat])

    templateBodyRenderContextFactory(enrichedTemplate) should equalTemplateBodyRenderContext(expectedBodyRenderContext)
  }
}
