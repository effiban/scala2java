package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, ModifiersRenderContext, StatContext}
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModListTraversalResultMockitoMatcher.eqModListTraversalResult
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.renderers.{ModListRenderer, PatListRenderer, TypeRenderer}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.ModListTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Decl, Init, Mod, Name, Type, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteType}

class DeclValTraverserImplTest extends UnitTestSuite {

  private val TheType = t"MyType"
  private val TheTraversedType = t"MyTraversedType"
  private val MyValPat = p"myVal"
  private val MyTraversedValPat = p"myTraversedVal"

  private val TheAnnot = Mod.Annot(
    Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
  )
  private val ScalaMods = List(TheAnnot)

  private val modListTraverser = mock[ModListTraverser]
  private val modifiersRenderContextFactory = mock[ModifiersRenderContextFactory]
  private val modListRenderer = mock[ModListRenderer]
  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val patTraverser = mock[PatTraverser]
  private val patListRenderer = mock[PatListRenderer]

  private val declValTraverser = new DeclValTraverserImpl(
    modListTraverser,
    modifiersRenderContextFactory,
    modListRenderer,
    typeTraverser,
    typeRenderer,
    patTraverser,
    patListRenderer
  )


  test("traverse() when it is a class member") {
    val javaScope = JavaScope.Class

    val declVal = Decl.Val(
      mods = ScalaMods,
      pats = List(MyValPat),
      decltpe = TheType
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Private, JavaModifier.Final))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Private, JavaModifier.Final))

    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(declVal, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |private final """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(TheTraversedType).when(typeTraverser).traverse(eqTree(TheType))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TheTraversedType))
    doReturn(MyTraversedValPat).when(patTraverser).traverse(eqTree(MyValPat))
    doWrite("myTraversedVal").when(patListRenderer).render(eqTreeList(List(MyTraversedValPat)))

    declValTraverser.traverse(declVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final MyTraversedType myTraversedVal""".stripMargin
  }

  test("traverse() when it is an interface member") {
    val javaScope = JavaScope.Interface

    val modifiers = List(TheAnnot)

    val declVal = Decl.Val(
      mods = modifiers,
      pats = List(MyValPat),
      decltpe = TheType
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods)
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods)

    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(declVal, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(TheTraversedType).when(typeTraverser).traverse(eqTree(TheType))
    doWrite("MyTraversedType").when(typeRenderer).render(eqTree(TheTraversedType))
    doReturn(MyTraversedValPat).when(patTraverser).traverse(eqTree(MyValPat))
    doWrite("myTraversedVal").when(patListRenderer).render(eqTreeList(List(MyTraversedValPat)))

    declValTraverser.traverse(declVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |MyTraversedType myTraversedVal""".stripMargin
  }

  private def eqExpectedScalaMods(declVal: Decl.Val, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(declVal, JavaTreeType.Variable, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
