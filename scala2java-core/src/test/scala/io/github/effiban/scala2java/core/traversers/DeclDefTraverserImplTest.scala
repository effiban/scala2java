package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, ModifiersRenderContext, StatContext}
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModListTraversalResultMockitoMatcher.eqModListTraversalResult
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.renderers.{ModListRenderer, TermNameRenderer, TypeRenderer}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.traversers.results.ModListTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Type.Bounds
import scala.meta.{Decl, Init, Mod, Name, Term, Type, XtensionQuasiquoteType}

class DeclDefTraverserImplTest extends UnitTestSuite {

  private val MethodType = t"MethodType"
  private val TraversedMethodType = t"TraversedMethodType"
  private val MethodName: Term.Name = Term.Name("myMethod")

  private val ScalaMods: List[Mod] = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )

  private val TypeParams = List(
    Type.Param(
      mods = List(),
      name = Type.Name("T"),
      tparams = List(),
      tbounds = Bounds(lo = None, hi = None),
      vbounds = List(),
      cbounds = List()
    )
  )

  private val MethodParams1 = List(
    termParamInt("param1"),
    termParamInt("param2")
  )
  private val MethodParams2 = List(
    termParamInt("param3"),
    termParamInt("param4")
  )

  private val modListTraverser = mock[ModListTraverser]
  private val modifiersRenderContextFactory = mock[ModifiersRenderContextFactory]
  private val modListRenderer = mock[ModListRenderer]
  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val termNameRenderer = mock[TermNameRenderer]
  private val termParamListTraverser = mock[DeprecatedTermParamListTraverser]

  private val declDefTraverser = new DeclDefTraverserImpl(
    modListTraverser,
    modifiersRenderContextFactory,
    modListRenderer,
    typeParamListTraverser,
    typeTraverser,
    typeRenderer,
    termNameRenderer,
    termParamListTraverser)


  test("traverse() for class method when has one list of params") {
    val javaScope = JavaScope.Class

    val declDef = Decl.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = MethodType
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(declDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(TraversedMethodType).when(typeTraverser).traverse(eqTree(MethodType))
    doWrite("TraversedMethodType").when(typeRenderer).render(eqTree(TraversedMethodType))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )

    declDefTraverser.traverse(declDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public TraversedMethodType myMethod(int param1, int param2)""".stripMargin
  }

  test("traverse() for class method when has type params") {
    val javaScope = JavaScope.Class

    val declDef = Decl.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = TypeParams,
      paramss = List(MethodParams1),
      decltpe = MethodType
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(declDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doWrite("<T>").when(typeParamListTraverser).traverse(eqTreeList(TypeParams))
    doReturn(TraversedMethodType).when(typeTraverser).traverse(eqTree(MethodType))
    doWrite("TraversedMethodType").when(typeRenderer).render(eqTree(TraversedMethodType))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )

    declDefTraverser.traverse(declDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public <T> TraversedMethodType myMethod(int param1, int param2)""".stripMargin
  }

  test("traverse() for interface method when has one list of params") {
    val javaScope = JavaScope.Interface

    val declDef = Decl.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1),
      decltpe = MethodType
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods)
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods)

    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(declDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(TraversedMethodType).when(typeTraverser).traverse(eqTree(MethodType))
    doWrite("TraversedMethodType").when(typeRenderer).render(eqTree(TraversedMethodType))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doWrite("(int param1, int param2)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )

    declDefTraverser.traverse(declDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |TraversedMethodType myMethod(int param1, int param2)""".stripMargin
  }

  test("traverse() for interface method when has two lists of params") {
    val javaScope = JavaScope.Interface

    val declDef = Decl.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParams1, MethodParams2),
      decltpe = MethodType
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods)
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods)

    doReturn(expectedModListTraversalResult).when(modListTraverser).traverse(eqExpectedScalaMods(declDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(TraversedMethodType).when(typeTraverser).traverse(eqTree(MethodType))
    doWrite("TraversedMethodType").when(typeRenderer).render(eqTree(TraversedMethodType))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doWrite("(int param1, int param2, int param3, int param4)").when(termParamListTraverser).traverse(
      termParams = eqTreeList(MethodParams1 ++ MethodParams2),
      context = ArgumentMatchers.eq(StatContext(JavaScope.MethodSignature)),
      onSameLine = ArgumentMatchers.eq(false)
    )

    declDefTraverser.traverse(declDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |TraversedMethodType myMethod(int param1, int param2, int param3, int param4)""".stripMargin
  }

  private def termParamInt(name: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(TypeNames.Int), default = None)
  }

  private def eqExpectedScalaMods(declDef: Decl.Def, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(declDef, JavaTreeType.Method, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
