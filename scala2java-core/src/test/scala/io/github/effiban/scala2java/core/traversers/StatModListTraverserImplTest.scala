package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.Mod.Annot
import scala.meta.{Mod, Name, XtensionQuasiquoteMod}

class StatModListTraverserImplTest extends UnitTestSuite {

  private val Annot1 = mod"@Annot1"
  private val Annot2 = mod"@Annot2"
  private val Annots = List(Annot1, Annot2)

  private val TraversedAnnot1 = mod"@TraversedAnnot1"
  private val TraversedAnnot2 = mod"@TraversedAnnot2"
  private val TraversedAnnots = List(TraversedAnnot1, TraversedAnnot2)

  private val PrivateFinalMods = List(Mod.Private(Name.Anonymous()), Mod.Final())

  private val annotTraverser = mock[AnnotTraverser]

  private val statModListTraverser = new StatModListTraverserImpl(annotTraverser)

  test("traverse List[Mod] when has only annotations") {
    expectTraverseAnnot()
    statModListTraverser.traverse(Annots).structure shouldBe TraversedAnnots.structure
  }

  test("traverse List[Mod] when has only visibility modifiers") {
    statModListTraverser.traverse(PrivateFinalMods).structure shouldBe PrivateFinalMods.structure
  }

  test("traverse List[Mod] when has annotations and visibility modifiers") {
    val mods = Annots ++ PrivateFinalMods

    expectTraverseAnnot()

    statModListTraverser.traverse(mods).structure shouldBe (TraversedAnnots ++ PrivateFinalMods).structure
  }

  private def expectTraverseAnnot() = {
    doAnswer((annot: Annot) => annot match {
      case anAnnot if anAnnot.structure == Annot1.structure => TraversedAnnot1
      case anAnnot if anAnnot.structure == Annot2.structure => TraversedAnnot2
    }).when(annotTraverser).traverse(any[Annot])
  }
}
