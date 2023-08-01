package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedModList
import io.github.effiban.scala2java.core.enrichers.entities.matchers.EnrichedModListScalatestMatcher.equalEnrichedModList
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Mod, Name}

class ModListEnricherImplTest extends UnitTestSuite {

  private val javaModifiersResolver = mock[JavaModifiersResolver]
  private val modifiersContext = mock[ModifiersContext]

  private val modListEnricher = new ModListEnricherImpl(javaModifiersResolver)

  test("enrich") {
    val scalaMods = List(Mod.Private(Name.Anonymous()), Mod.Final())
    val expectedJavaModifiers = List(JavaModifier.Private, JavaModifier.Final)
    val expectedEnrichedModList = EnrichedModList(scalaMods, expectedJavaModifiers)

    when(modifiersContext.scalaMods).thenReturn(scalaMods)
    when(javaModifiersResolver.resolve(modifiersContext)).thenReturn(expectedJavaModifiers)

    modListEnricher.enrich(modifiersContext) should equalEnrichedModList(expectedEnrichedModList)
  }
}
