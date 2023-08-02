package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedDeclDef
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedDeclDefScalatestMatcher.equalEnrichedDeclDef
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.XtensionQuasiquoteTerm

class DeclDefEnricherImplTest extends UnitTestSuite {

  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val declDefEnricher = new DeclDefEnricherImpl(javaModifiersResolver)

  test("enrich") {
    val javaScope = JavaScope.Class
    val declDef = q"private def foo(x: Int)"
    val expectedJavaModifiers = List(JavaModifier.Private)
    val expectedModifiersContext = ModifiersContext(declDef, JavaTreeType.Method, javaScope)
    val expectedEnrichedDeclDef = EnrichedDeclDef(declDef, expectedJavaModifiers)

    when(javaModifiersResolver.resolve(eqModifiersContext(expectedModifiersContext))).thenReturn(expectedJavaModifiers)

    declDefEnricher.enrich(declDef, StatContext(javaScope)) should equalEnrichedDeclDef(expectedEnrichedDeclDef)
  }
}
