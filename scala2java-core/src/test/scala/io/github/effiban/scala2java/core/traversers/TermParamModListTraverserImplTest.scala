package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.resolvers.JavaExtraModifierResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.Mod.Annot
import scala.meta.{Mod, Term, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TermParamModListTraverserImplTest extends UnitTestSuite {

  private val Annot1 = mod"@Annot1"
  private val Annot2 = mod"@Annot2"
  private val Annots = List(Annot1, Annot2)

  private val TraversedAnnot1 = mod"@TraversedAnnot1"
  private val TraversedAnnot2 = mod"@TraversedAnnot2"
  private val TraversedAnnots = List(TraversedAnnot1, TraversedAnnot2)

  private val annotTraverser = mock[AnnotTraverser]
  private val javaFinalModifierResolver = mock[JavaExtraModifierResolver]

  private val termParamModListTraverser = new TermParamModListTraverserImpl(annotTraverser, javaFinalModifierResolver)

  test("traverse when has no mods and resolver returns None") {
    val javaScope = JavaScope.MethodSignature
    val termParam = termParamWith(Nil)
    val modifiersContext = modifiersContextOf(termParam, javaScope)

    doAnswer((annot: Annot) => annot match {
      case anAnnot if anAnnot.structure == Annot1.structure => TraversedAnnot1
      case anAnnot if anAnnot.structure == Annot2.structure => TraversedAnnot2
      case anAnnot => anAnnot
    }).when(annotTraverser).traverse(any[Annot])

    when(javaFinalModifierResolver.resolve(eqModifiersContext(modifiersContext))).thenReturn(None)

    termParamModListTraverser.traverse(termParam, javaScope) shouldBe Nil
  }

  test("traverse when has no mods and resolver returns 'final'") {
    val javaScope = JavaScope.MethodSignature
    val termParam = termParamWith(Nil)
    val modifiersContext = modifiersContextOf(termParam, javaScope)
    val expectedTraversedMods = List(Mod.Final())

    doAnswer((annot: Annot) => annot match {
      case anAnnot if anAnnot.structure == Annot1.structure => TraversedAnnot1
      case anAnnot if anAnnot.structure == Annot2.structure => TraversedAnnot2
      case anAnnot => anAnnot
    }).when(annotTraverser).traverse(any[Annot])

    when(javaFinalModifierResolver.resolve(eqModifiersContext(modifiersContext))).thenReturn(Some(JavaModifier.Final))

    termParamModListTraverser.traverse(termParam, javaScope).structure shouldBe expectedTraversedMods.structure
  }

  test("traverse when has only annotations and resolver returns None") {
    val javaScope = JavaScope.MethodSignature
    val termParam = termParamWith(Annots)
    val modifiersContext = modifiersContextOf(termParam, javaScope)
    val expectedTraversedMods = TraversedAnnots

    doAnswer((annot: Annot) => annot match {
      case anAnnot if anAnnot.structure == Annot1.structure => TraversedAnnot1
      case anAnnot if anAnnot.structure == Annot2.structure => TraversedAnnot2
      case anAnnot => anAnnot
    }).when(annotTraverser).traverse(any[Annot])

    when(javaFinalModifierResolver.resolve(eqModifiersContext(modifiersContext))).thenReturn(None)

    termParamModListTraverser.traverse(termParam, javaScope).structure shouldBe expectedTraversedMods.structure
  }

  test("traverse when has only annotations and resolver returns 'final'") {
    val javaScope = JavaScope.MethodSignature
    val termParam = termParamWith(Annots)
    val modifiersContext = modifiersContextOf(termParam, javaScope)
    val expectedTraversedMods = TraversedAnnots :+ Mod.Final()

    doAnswer((annot: Annot) => annot match {
      case anAnnot if anAnnot.structure == Annot1.structure => TraversedAnnot1
      case anAnnot if anAnnot.structure == Annot2.structure => TraversedAnnot2
      case anAnnot => anAnnot
    }).when(annotTraverser).traverse(any[Annot])

    when(javaFinalModifierResolver.resolve(eqModifiersContext(modifiersContext))).thenReturn(Some(JavaModifier.Final))

    termParamModListTraverser.traverse(termParam, javaScope).structure shouldBe expectedTraversedMods.structure
  }

  test("traverse when has only visibility modifiers without 'final' and resolver returns 'final'") {
    val javaScope = JavaScope.MethodSignature
    val mods = List(mod"implicit")
    val termParam = termParamWith(mods)
    val modifiersContext = modifiersContextOf(termParam, javaScope)
    val expectedTraversedMods = mods :+ Mod.Final()

    when(javaFinalModifierResolver.resolve(eqModifiersContext(modifiersContext))).thenReturn(Some(JavaModifier.Final))

    termParamModListTraverser.traverse(termParam, javaScope).structure shouldBe expectedTraversedMods.structure
  }

  test("traverse when has only visibility modifiers without 'final' and resolver returns None") {
    val javaScope = JavaScope.LambdaSignature
    val mods = List(mod"implicit")
    val termParam = termParamWith(mods)
    val modifiersContext = modifiersContextOf(termParam, javaScope)
    val expectedTraversedMods = mods

    when(javaFinalModifierResolver.resolve(eqModifiersContext(modifiersContext))).thenReturn(None)

    termParamModListTraverser.traverse(termParam, javaScope).structure shouldBe expectedTraversedMods.structure
  }

  test("traverse when has annotations and visibility modifiers without 'final', and resolver returns final") {
    val javaScope = JavaScope.MethodSignature
    val nonAnnotsMods = List(mod"implicit")
    val initialMods = Annots ++ nonAnnotsMods
    val termParam = termParamWith(initialMods)
    val modifiersContext = modifiersContextOf(termParam, javaScope)
    val expectedTraversedMods = TraversedAnnots ++ nonAnnotsMods :+ Mod.Final()

    doAnswer((annot: Annot) => annot match {
      case anAnnot if anAnnot.structure == Annot1.structure => TraversedAnnot1
      case anAnnot if anAnnot.structure == Annot2.structure => TraversedAnnot2
      case anAnnot => anAnnot
    }).when(annotTraverser).traverse(any[Annot])

    when(javaFinalModifierResolver.resolve(eqModifiersContext(modifiersContext))).thenReturn(Some(JavaModifier.Final))

    termParamModListTraverser.traverse(termParam, javaScope).structure shouldBe expectedTraversedMods.structure
  }

  private def modifiersContextOf(termParam: Term.Param, javaScope: JavaScope) = ModifiersContext(termParam, JavaTreeType.Parameter, javaScope)

  private def termParamWith(mods: List[Mod]) =
    Term.Param(
      mods = mods,
      name = q"aParam",
      decltpe = Some(t"Int"),
      default = None
    )

}
