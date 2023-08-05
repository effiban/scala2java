package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities._
import io.github.effiban.scala2java.core.entities.{JavaModifier, SealedHierarchies}
import io.github.effiban.scala2java.core.renderers.contexts._
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DefnRenderContextFactoryImplTest extends UnitTestSuite {

  private val TheJavaModifiers = List(JavaModifier.Public, JavaModifier.Final)

  private val TheTraitName = t"MyTrait"
  private val TheCaseClassName = t"MyCaseClass"
  private val TheRegularClassName = t"MyRegularClass"
  private val TheObjectName = q"MyObject"

  private val ThePermittedSubTypeNames = List(t"Sub1", t"Sub2")

  private val enrichedDefnVar = mock[EnrichedDefnVar]
  private val enrichedDefnDef = mock[EnrichedDefnDef]
  private val enrichedTrait = mock[EnrichedTrait]
  private val enrichedCaseClass = mock[EnrichedCaseClass]
  private val enrichedRegularClass = mock[EnrichedRegularClass]
  private val enrichedObject = mock[EnrichedObject]

  private val traitRenderContext = mock[TraitRenderContext]
  private val caseClassRenderContext = mock[CaseClassRenderContext]
  private val regularClassRenderContext = mock[RegularClassRenderContext]
  private val objectRenderContext = mock[ObjectRenderContext]

  private val traitRenderContextFactory = mock[TraitRenderContextFactory]
  private val caseClassRenderContextFactory = mock[CaseClassRenderContextFactory]
  private val regularClassRenderContextFactory = mock[RegularClassRenderContextFactory]
  private val objectRenderContextFactory = mock[ObjectRenderContextFactory]

  private val defnRenderContextFactory = new DefnRenderContextFactoryImpl(
    traitRenderContextFactory,
    caseClassRenderContextFactory,
    regularClassRenderContextFactory,
    objectRenderContextFactory
  )

  override def beforeEach(): Unit = {
    when(enrichedTrait.name).thenReturn(TheTraitName)
    when(enrichedCaseClass.name).thenReturn(TheCaseClassName)
    when(enrichedRegularClass.name).thenReturn(TheRegularClassName)
    when(enrichedObject.name).thenReturn(TheObjectName)
  }

  test("apply() for EnrichedDefnVar when has Java modifiers") {
    when(enrichedDefnVar.javaModifiers).thenReturn(TheJavaModifiers)

    defnRenderContextFactory(enrichedDefnVar) shouldBe VarRenderContext(TheJavaModifiers)
  }

  test("apply() for EnrichedDefnVar when has no Java modifiers") {
    when(enrichedDefnVar.javaModifiers).thenReturn(Nil)

    defnRenderContextFactory(enrichedDefnVar, SealedHierarchies()) shouldBe VarRenderContext()
  }

  test("apply() for EnrichedDefnDef when has Java modifiers") {
    when(enrichedDefnDef.javaModifiers).thenReturn(TheJavaModifiers)

    defnRenderContextFactory(enrichedDefnDef, SealedHierarchies()) shouldBe DefRenderContext(TheJavaModifiers)
  }

  test("apply() for EnrichedDefnDef when has no Java modifiers") {
    when(enrichedDefnDef.javaModifiers).thenReturn(Nil)

    defnRenderContextFactory(enrichedDefnDef, SealedHierarchies()) shouldBe DefRenderContext()
  }

  test("apply() for EnrichedTrait when trait has permitted sub-types") {
    val sealedHierarchies = SealedHierarchies(
      Map(TheTraitName -> ThePermittedSubTypeNames)
    )
    when(traitRenderContextFactory(eqTo(enrichedTrait), eqTreeList(ThePermittedSubTypeNames)))
      .thenReturn(traitRenderContext)

    defnRenderContextFactory(enrichedTrait, sealedHierarchies) shouldBe traitRenderContext
  }

  test("apply() for EnrichedTrait when trait has no permitted sub-types") {
    val sealedHierarchies = SealedHierarchies(
      Map(t"Blabla" -> ThePermittedSubTypeNames)
    )
    when(traitRenderContextFactory(eqTo(enrichedTrait), eqTo(Nil))).thenReturn(traitRenderContext)

    defnRenderContextFactory(enrichedTrait, sealedHierarchies) shouldBe traitRenderContext
  }

  test("apply() for EnrichedCaseClass") {
    when(caseClassRenderContextFactory(eqTo(enrichedCaseClass))).thenReturn(caseClassRenderContext)

    defnRenderContextFactory(enrichedCaseClass, SealedHierarchies()) shouldBe caseClassRenderContext
  }

  test("apply() for EnrichedRegularClass when class has permitted sub-types") {
    val sealedHierarchies = SealedHierarchies(
      Map(TheRegularClassName -> ThePermittedSubTypeNames)
    )
    when(regularClassRenderContextFactory(eqTo(enrichedRegularClass), eqTreeList(ThePermittedSubTypeNames)))
      .thenReturn(regularClassRenderContext)

    defnRenderContextFactory(enrichedRegularClass, sealedHierarchies) shouldBe regularClassRenderContext
  }

  test("apply() for EnrichedRegularClass when class has no permitted sub-types") {
    val sealedHierarchies = SealedHierarchies(
      Map(t"Blabla" -> ThePermittedSubTypeNames)
    )
    when(regularClassRenderContextFactory(eqTo(enrichedRegularClass), eqTo(Nil))).thenReturn(regularClassRenderContext)

    defnRenderContextFactory(enrichedRegularClass, sealedHierarchies) shouldBe regularClassRenderContext
  }

  test("apply() for EnrichedObject") {
    when(objectRenderContextFactory(eqTo(enrichedObject))).thenReturn(objectRenderContext)

    defnRenderContextFactory(enrichedObject, SealedHierarchies()) shouldBe objectRenderContext
  }
}
