package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.{JavaModifier, SealedHierarchies}
import io.github.effiban.scala2java.core.renderers.contexts._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results._
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DefnRenderContextFactoryImplTest extends UnitTestSuite {

  private val TheJavaModifiers = List(JavaModifier.Public, JavaModifier.Final)

  private val TheTraitName = t"MyTrait"
  private val TheCaseClassName = t"MyCaseClass"
  private val TheObjectName = q"MyObject"

  private val ThePermittedSubTypeNames = List(t"Sub1", t"Sub2")

  private val defnVarTraversalResult = mock[DefnVarTraversalResult]
  private val defnDefTraversalResult = mock[DefnDefTraversalResult]
  private val traitTraversalResult = mock[TraitTraversalResult]
  private val caseClassTraversalResult = mock[CaseClassTraversalResult]
  private val objectTraversalResult = mock[ObjectTraversalResult]

  private val traitRenderContext = mock[TraitRenderContext]
  private val caseClassRenderContext = mock[CaseClassRenderContext]
  private val objectRenderContext = mock[ObjectRenderContext]

  private val traitRenderContextFactory = mock[TraitRenderContextFactory]
  private val caseClassRenderContextFactory = mock[CaseClassRenderContextFactory]
  private val objectRenderContextFactory = mock[ObjectRenderContextFactory]

  private val defnRenderContextFactory = new DefnRenderContextFactoryImpl(
    traitRenderContextFactory,
    caseClassRenderContextFactory,
    objectRenderContextFactory
  )

  override def beforeEach(): Unit = {
    when(traitTraversalResult.name).thenReturn(TheTraitName)
    when(caseClassTraversalResult.name).thenReturn(TheCaseClassName)
    when(objectTraversalResult.name).thenReturn(TheObjectName)
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

  test("apply() for CaseClassTraversalResult") {
    when(caseClassRenderContextFactory(eqTo(caseClassTraversalResult))).thenReturn(caseClassRenderContext)

    defnRenderContextFactory(caseClassTraversalResult) shouldBe caseClassRenderContext
  }

  test("apply() for ObjectTraversalResult") {
    when(objectRenderContextFactory(eqTo(objectTraversalResult))).thenReturn(objectRenderContext)

    defnRenderContextFactory(objectTraversalResult) shouldBe objectRenderContext
  }
}
