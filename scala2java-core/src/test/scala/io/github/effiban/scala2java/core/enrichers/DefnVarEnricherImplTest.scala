package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedDefnVar
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedDefnVarScalatestMatcher.equalEnrichedDefnVar
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.XtensionQuasiquoteTerm

class DefnVarEnricherImplTest extends UnitTestSuite {

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val defnVarEnricher = new DefnVarEnricherImpl(javaModifiersResolver)

  test("enrich") {
    val javaScope = JavaScope.Class
    val defnVar = q"private final var x: Int = 3"
    val expectedJavaModifiers = List(JavaModifier.Private, JavaModifier.Final)
    val expectedModifiersContext = ModifiersContext(defnVar, JavaTreeType.Variable, javaScope)
    val expectedEnrichedDefnVar = EnrichedDefnVar(defnVar, expectedJavaModifiers)

    when(javaModifiersResolver.resolve(eqModifiersContext(expectedModifiersContext))).thenReturn(expectedJavaModifiers)

    defnVarEnricher.enrich(defnVar, StatContext(javaScope)) should equalEnrichedDefnVar(expectedEnrichedDefnVar)
  }
}
