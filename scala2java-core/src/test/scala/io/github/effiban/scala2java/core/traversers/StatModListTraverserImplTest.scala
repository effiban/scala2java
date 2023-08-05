package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.traversers.results.ModListTraversalResult
import io.github.effiban.scala2java.core.traversers.results.matchers.ModListTraversalResultScalatestMatcher.equalModListTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.Mod.Annot
import scala.meta.{Decl, Mod, Name, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteMod}

class StatModListTraverserImplTest extends UnitTestSuite {

  private val Pats = List(p"x")

  private val Annot1 = mod"@Annot1"
  private val Annot2 = mod"@Annot2"
  private val Annots = List(Annot1, Annot2)

  private val TraversedAnnot1 = mod"@TraversedAnnot1"
  private val TraversedAnnot2 = mod"@TraversedAnnot2"
  private val TraversedAnnots = List(TraversedAnnot1, TraversedAnnot2)

  private val PrivateFinalMods = List(Mod.Private(Name.Anonymous()), Mod.Final())
  private val JavaPrivateFinalMods = List(JavaModifier.Private, JavaModifier.Final)

  private val annotTraverser = mock[AnnotTraverser]
  private val javaModifiersResolver = mock[JavaModifiersResolver]

  private val statModListTraverser = new StatModListTraverserImpl(annotTraverser, javaModifiersResolver)

  test("traverse ModifiersContext when has only annotations") {
    val modifiersContext = modifiersContextOf(Annots)

    val expectedResult = ModListTraversalResult(scalaMods = TraversedAnnots)

    expectTraverseAnnot()
    when(javaModifiersResolver.resolve(eqModifiersContext(modifiersContext))).thenReturn(Nil)

    statModListTraverser.traverse(modifiersContext) should equalModListTraversalResult(expectedResult)
  }

  test("traverse List[Mod] when has only annotations") {
    expectTraverseAnnot()
    statModListTraverser.traverse(Annots).structure shouldBe TraversedAnnots.structure
  }

  private def expectTraverseAnnot() = {
    doAnswer((annot: Annot) => annot match {
      case anAnnot if anAnnot.structure == Annot1.structure => TraversedAnnot1
      case anAnnot if anAnnot.structure == Annot2.structure => TraversedAnnot2
    }).when(annotTraverser).traverse(any[Annot])
  }

  test("traverse ModifiersContext when has only visibility modifiers") {
    val modifiersContext = modifiersContextOf(PrivateFinalMods)

    val expectedResult = ModListTraversalResult(scalaMods = PrivateFinalMods, javaModifiers = JavaPrivateFinalMods)

    when(javaModifiersResolver.resolve(eqModifiersContext(modifiersContext))).thenReturn(JavaPrivateFinalMods)

    statModListTraverser.traverse(modifiersContext) should equalModListTraversalResult(expectedResult)
  }

  test("traverse List[Mod] when has only visibility modifiers") {
    statModListTraverser.traverse(PrivateFinalMods).structure shouldBe PrivateFinalMods.structure
  }

  test("traverse ModifiersContext when has annotations and visibility modifiers") {
    val scalaMods = Annots ++ PrivateFinalMods
    val traversedScalaMods = TraversedAnnots ++ PrivateFinalMods
    val modifiersContext = modifiersContextOf(scalaMods)

    val expectedResult = ModListTraversalResult(scalaMods = traversedScalaMods, javaModifiers = JavaPrivateFinalMods)
    expectTraverseAnnot()

    when(javaModifiersResolver.resolve(eqModifiersContext(modifiersContext))).thenReturn(JavaPrivateFinalMods)

    statModListTraverser.traverse(modifiersContext) should equalModListTraversalResult(expectedResult)
  }

  test("traverse List[Mod] when has annotations and visibility modifiers") {
    val mods = Annots ++ PrivateFinalMods

    expectTraverseAnnot()

    statModListTraverser.traverse(mods).structure shouldBe (TraversedAnnots ++ PrivateFinalMods).structure
  }

  private def modifiersContextOf(mods: List[Mod]) = ModifiersContext(declValWith(mods), JavaTreeType.Variable, JavaScope.Class)

  private def declValWith(mods: List[Mod]) = Decl.Val(mods, Pats, TypeNames.Int)
}
