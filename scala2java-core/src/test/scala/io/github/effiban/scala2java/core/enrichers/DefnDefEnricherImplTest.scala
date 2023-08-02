package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedDefnDef
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedDefnDefScalatestMatcher.equalEnrichedDefnDef
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.XtensionQuasiquoteTerm

class DefnDefEnricherImplTest extends UnitTestSuite {

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val defnDefEnricher = new DefnDefEnricherImpl(javaModifiersResolver)

  test("enrich") {
    val javaScope = JavaScope.Class
    val defnDef = q"private def foo(x: Int) = x + 1"
    val expectedJavaModifiers = List(JavaModifier.Private)
    val expectedModifiersContext = ModifiersContext(defnDef, JavaTreeType.Method, javaScope)
    val expectedEnrichedDefnDef = EnrichedDefnDef(defnDef, expectedJavaModifiers)

    when(javaModifiersResolver.resolve(eqModifiersContext(expectedModifiersContext))).thenReturn(expectedJavaModifiers)

    defnDefEnricher.enrich(defnDef, StatContext(javaScope)) should equalEnrichedDefnDef(expectedEnrichedDefnDef)
  }
}
