package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.TraitRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.TraitTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteType

class DefnRenderContextFactoryImplTest extends UnitTestSuite {

  private val TheTraitName = t"A"
  private val ThePermittedSubTypeNames = List(t"A1", t"A2")


  private val traitTraversalResult = mock[TraitTraversalResult]
  private val traitRenderContext = mock[TraitRenderContext]
  private val traitRenderContextFactory = mock[TraitRenderContextFactory]

  private val defnRenderContextFactory = new DefnRenderContextFactoryImpl(traitRenderContextFactory)

  override def beforeEach(): Unit = {
    when(traitTraversalResult.name).thenReturn(TheTraitName)
  }

  test("apply() for trait when trait has permitted sub-types") {
    val sealedHierarchies = SealedHierarchies(
      Map(TheTraitName -> ThePermittedSubTypeNames)
    )
    when(traitRenderContextFactory(eqTo(traitTraversalResult), eqTreeList(ThePermittedSubTypeNames)))
      .thenReturn(traitRenderContext)

    defnRenderContextFactory(traitTraversalResult, sealedHierarchies) shouldBe traitRenderContext
  }

  test("apply() for trait when trait has no permitted sub-types") {
    val sealedHierarchies = SealedHierarchies(
      Map(t"Blabla" -> ThePermittedSubTypeNames)
    )
    when(traitRenderContextFactory(eqTo(traitTraversalResult), eqTo(Nil))).thenReturn(traitRenderContext)

    defnRenderContextFactory(traitTraversalResult, sealedHierarchies) shouldBe traitRenderContext
  }
}
