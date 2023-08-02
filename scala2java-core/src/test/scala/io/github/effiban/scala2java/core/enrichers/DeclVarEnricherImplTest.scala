package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedDeclVar
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedDeclVarScalatestMatcher.equalEnrichedDeclVar
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.XtensionQuasiquoteTerm

class DeclVarEnricherImplTest extends UnitTestSuite {

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val declVarEnricher = new DeclVarEnricherImpl(javaModifiersResolver)

  test("enrich") {
    val javaScope = JavaScope.Class
    val declVar = q"private final var x: Int"
    val expectedJavaModifiers = List(JavaModifier.Private, JavaModifier.Final)
    val expectedModifiersContext = ModifiersContext(declVar, JavaTreeType.Variable, javaScope)
    val expectedEnrichedDeclVar = EnrichedDeclVar(declVar, expectedJavaModifiers)

    when(javaModifiersResolver.resolve(eqModifiersContext(expectedModifiersContext))).thenReturn(expectedJavaModifiers)

    declVarEnricher.enrich(declVar, StatContext(javaScope)) should equalEnrichedDeclVar(expectedEnrichedDeclVar)
  }
}
