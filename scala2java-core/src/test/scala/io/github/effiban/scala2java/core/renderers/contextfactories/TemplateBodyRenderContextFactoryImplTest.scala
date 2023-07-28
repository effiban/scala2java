package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.{TemplateBodyRenderContext, TemplateStatRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.TemplateBodyRenderContextScalatestMatcher.equalTemplateBodyRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{PopulatedStatTraversalResult, TemplateTraversalResult}
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.XtensionQuasiquoteTerm

class TemplateBodyRenderContextFactoryImplTest extends UnitTestSuite {

  private val templateStatRenderContextFactory = mock[TemplateStatRenderContextFactory]

  private val templateBodyRenderContextFactory = new TemplateBodyRenderContextFactoryImpl(templateStatRenderContextFactory)

  test("apply") {
    val stat1 = q"foo(1)"
    val stat2 = q"foo(2)"

    val statResult1 = mock[PopulatedStatTraversalResult]
    val statResult2 = mock[PopulatedStatTraversalResult]

    val statRenderContext1 = mock[TemplateStatRenderContext]
    val statRenderContext2 = mock[TemplateStatRenderContext]

    val templateTraversalResult = mock[TemplateTraversalResult]

    when(templateTraversalResult.statResults).thenReturn(List(statResult1, statResult2))
    when(statResult1.tree).thenReturn(stat1)
    when(statResult2.tree).thenReturn(stat2)

    val expectedBodyRenderContext = TemplateBodyRenderContext(
      Map(stat1 -> statRenderContext1, stat2 -> statRenderContext2)
    )

    doAnswer((statResult: PopulatedStatTraversalResult) => statResult match {
      case aStatResult if aStatResult == statResult1 => statRenderContext1
      case aStatResult if aStatResult == statResult2 => statRenderContext2
    }).when(templateStatRenderContextFactory)(any[PopulatedStatTraversalResult])

    templateBodyRenderContextFactory(templateTraversalResult) should equalTemplateBodyRenderContext(expectedBodyRenderContext)
  }

}
