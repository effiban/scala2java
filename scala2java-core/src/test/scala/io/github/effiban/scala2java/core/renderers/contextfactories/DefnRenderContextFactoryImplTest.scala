package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.{JavaModifier, SealedHierarchies}
import io.github.effiban.scala2java.core.renderers.contexts.{DefRenderContext, TraitRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{DefnDefTraversalResult, DefnVarTraversalResult, TraitTraversalResult}
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteType

class DefnRenderContextFactoryImplTest extends UnitTestSuite {

  private val TheJavaModifiers = List(JavaModifier.Public, JavaModifier.Final)

  private val TheTraitName = t"A"

  private val ThePermittedSubTypeNames = List(t"A1", t"A2")

  private val defnVarTraversalResult = mock[DefnVarTraversalResult]
  private val defnDefTraversalResult = mock[DefnDefTraversalResult]
  private val traitTraversalResult = mock[TraitTraversalResult]

  private val traitRenderContext = mock[TraitRenderContext]

  private val traitRenderContextFactory = mock[TraitRenderContextFactory]

  private val defnRenderContextFactory = new DefnRenderContextFactoryImpl(traitRenderContextFactory)

  override def beforeEach(): Unit = {
    when(traitTraversalResult.name).thenReturn(TheTraitName)
  }

  test("apply() for DefnVarTraversalResult when has Java modifiers") {
    when(defnVarTraversalResult.javaModifiers).thenReturn(TheJavaModifiers)

    defnRenderContextFactory(defnVarTraversalResult) shouldBe VarRenderContext(TheJavaModifiers)
  }

  test("apply() for DefnVarTraversalResult when has no Java modifiers") {
    when(defnVarTraversalResult.javaModifiers).thenReturn(Nil)

    defnRenderContextFactory(defnVarTraversalResult) shouldBe VarRenderContext()
  }

  test("apply() for DefnDefTraversalResult when has Java modifiers") {
    when(defnDefTraversalResult.javaModifiers).thenReturn(TheJavaModifiers)

    defnRenderContextFactory(defnDefTraversalResult) shouldBe DefRenderContext(TheJavaModifiers)
  }

  test("apply() for DefnDefTraversalResult when has no Java modifiers") {
    when(defnDefTraversalResult.javaModifiers).thenReturn(Nil)

    defnRenderContextFactory(defnDefTraversalResult) shouldBe DefRenderContext()
  }

  test("apply() for TraitTraversalResult when trait has permitted sub-types") {
    val sealedHierarchies = SealedHierarchies(
      Map(TheTraitName -> ThePermittedSubTypeNames)
    )
    when(traitRenderContextFactory(eqTo(traitTraversalResult), eqTreeList(ThePermittedSubTypeNames)))
      .thenReturn(traitRenderContext)

    defnRenderContextFactory(traitTraversalResult, sealedHierarchies) shouldBe traitRenderContext
  }

  test("apply() for TraitTraversalResult when trait has no permitted sub-types") {
    val sealedHierarchies = SealedHierarchies(
      Map(t"Blabla" -> ThePermittedSubTypeNames)
    )
    when(traitRenderContextFactory(eqTo(traitTraversalResult), eqTo(Nil))).thenReturn(traitRenderContext)

    defnRenderContextFactory(traitTraversalResult, sealedHierarchies) shouldBe traitRenderContext
  }
}
