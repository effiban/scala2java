package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.ModListTraversalResultMockitoMatcher.eqModListTraversalResult
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.renderers._
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.renderers.contexts.{ModifiersRenderContext, TermParamListRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.ModifiersRenderContextMatcher.eqModifiersRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.ModListTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.{Decl, Init, Mod, Name, Term, Type, XtensionQuasiquoteTermParam, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class DeclDefTraverserImplTest extends UnitTestSuite {

  private val MethodType = t"MethodType"
  private val TraversedMethodType = t"TraversedMethodType"
  private val MethodName: Term.Name = Term.Name("myMethod")

  private val ScalaMods: List[Mod] = List(
    Mod.Annot(
      Init(tpe = Type.Name("MyAnnotation"), name = Name.Anonymous(), argss = List())
    )
  )

  private val TypeParam1 = tparam"T1"
  private val TypeParam2 = tparam"T2"
  private val TypeParams = List(TypeParam1, TypeParam2)

  private val TraversedTypeParam1 = tparam"T11"
  private val TraversedTypeParam2 = tparam"T22"
  private val TraversedTypeParams = List(TraversedTypeParam1, TraversedTypeParam2)

  private val MethodParam1 = param"param1: Int"
  private val MethodParam2 = param"param2: Int"
  private val MethodParam3 = param"param3: Int"
  private val MethodParam4 = param"param4: Int"

  private val MethodParamList1 = List(MethodParam1, MethodParam2)
  private val MethodParamList2 = List(MethodParam3, MethodParam4)

  private val TraversedMethodParam1 = param"param11: Int"
  private val TraversedMethodParam2 = param"param22: Int"
  private val TraversedMethodParam3 = param"param33: Int"
  private val TraversedMethodParam4 = param"param44: Int"

  private val TraversedMethodParamList1 = List(TraversedMethodParam1, TraversedMethodParam2)
  private val TraversedMethodParamList2 = List(TraversedMethodParam3, TraversedMethodParam4)

  private val statModListTraverser = mock[StatModListTraverser]
  private val modifiersRenderContextFactory = mock[ModifiersRenderContextFactory]
  private val modListRenderer = mock[ModListRenderer]
  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val typeParamTraverser = mock[TypeParamTraverser]
  private val typeParamListRenderer = mock[TypeParamListRenderer]
  private val termNameRenderer = mock[TermNameRenderer]
  private val termParamTraverser = mock[TermParamTraverser]
  private val termParamListRenderer = mock[TermParamListRenderer]

  private val declDefTraverser = new DeclDefTraverserImpl(
    statModListTraverser,
    modifiersRenderContextFactory,
    modListRenderer,
    typeParamTraverser,
    typeParamListRenderer,
    typeTraverser,
    typeRenderer,
    termNameRenderer,
    termParamTraverser,
    termParamListRenderer
  )


  test("traverse() for class method when has one list of params") {
    val javaScope = JavaScope.Class

    val declDef = Decl.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = MethodType
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    doReturn(expectedModListTraversalResult).when(statModListTraverser).traverse(eqExpectedScalaMods(declDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(TraversedMethodType).when(typeTraverser).traverse(eqTree(MethodType))
    doWrite("TraversedMethodType").when(typeRenderer).render(eqTree(TraversedMethodType))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
      case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(final int param11, final int param22)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedMethodParamList1),
      context = eqTo(TermParamListRenderContext())
    )

    declDefTraverser.traverse(declDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public TraversedMethodType myMethod(final int param11, final int param22)""".stripMargin
  }

  test("traverse() for class method when has type params") {
    val javaScope = JavaScope.Class

    val declDef = Decl.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = TypeParams,
      paramss = List(MethodParamList1),
      decltpe = MethodType
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods, javaModifiers = List(JavaModifier.Public))

    doReturn(expectedModListTraversalResult).when(statModListTraverser).traverse(eqExpectedScalaMods(declDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |public """.stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doAnswer((tparam: Type.Param) => tparam match {
      case aTypeParam if aTypeParam.structure == TypeParam1.structure => TraversedTypeParam1
      case aTypeParam if aTypeParam.structure == TypeParam2.structure => TraversedTypeParam2
      case aTypeParam => aTypeParam
    }).when(typeParamTraverser).traverse(any[Type.Param])
    doWrite("<T11, T22>").when(typeParamListRenderer).render(eqTreeList(TraversedTypeParams))
    doReturn(TraversedMethodType).when(typeTraverser).traverse(eqTree(MethodType))
    doWrite("TraversedMethodType").when(typeRenderer).render(eqTree(TraversedMethodType))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
      case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(final int param11, final int param22)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedMethodParamList1),
      context = eqTo(TermParamListRenderContext())
    )

    declDefTraverser.traverse(declDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |public <T11, T22> TraversedMethodType myMethod(final int param11, final int param22)""".stripMargin
  }

  test("traverse() for interface method when has one list of params") {
    val javaScope = JavaScope.Interface

    val declDef = Decl.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = MethodType
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods)
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods)

    doReturn(expectedModListTraversalResult).when(statModListTraverser).traverse(eqExpectedScalaMods(declDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(TraversedMethodType).when(typeTraverser).traverse(eqTree(MethodType))
    doWrite("TraversedMethodType").when(typeRenderer).render(eqTree(TraversedMethodType))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
      case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(int param11, int param22)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedMethodParamList1),
      context = eqTo(TermParamListRenderContext())
    )

    declDefTraverser.traverse(declDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |TraversedMethodType myMethod(int param11, int param22)""".stripMargin
  }

  test("traverse() for interface method when has two lists of params") {
    val javaScope = JavaScope.Interface

    val declDef = Decl.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1, MethodParamList2),
      decltpe = MethodType
    )

    val expectedModListTraversalResult = ModListTraversalResult(scalaMods = ScalaMods)
    val expectedModifiersRenderContext = ModifiersRenderContext(scalaMods = ScalaMods)

    doReturn(expectedModListTraversalResult).when(statModListTraverser).traverse(eqExpectedScalaMods(declDef, javaScope))
    doReturn(expectedModifiersRenderContext)
      .when(modifiersRenderContextFactory)(eqModListTraversalResult(expectedModListTraversalResult), annotsOnSameLine = eqTo(false))
    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListRenderer).render(eqModifiersRenderContext(expectedModifiersRenderContext))
    doReturn(TraversedMethodType).when(typeTraverser).traverse(eqTree(MethodType))
    doWrite("TraversedMethodType").when(typeRenderer).render(eqTree(TraversedMethodType))
    doWrite("myMethod").when(termNameRenderer).render(eqTree(MethodName))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
      case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      case aParam if aParam.structure == MethodParam3.structure => TraversedMethodParam3
      case aParam if aParam.structure == MethodParam4.structure => TraversedMethodParam4
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
    doWrite("(int param11, int param22, int param33, int param44)").when(termParamListRenderer).render(
      termParams = eqTreeList(TraversedMethodParamList1 ++ TraversedMethodParamList2),
      context = eqTo(TermParamListRenderContext())
    )

    declDefTraverser.traverse(declDef, StatContext(javaScope))

    outputWriter.toString shouldBe
      """
        |@MyAnnotation
        |TraversedMethodType myMethod(int param11, int param22, int param33, int param44)""".stripMargin
  }

  private def eqExpectedScalaMods(declDef: Decl.Def, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(declDef, JavaTreeType.Method, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
