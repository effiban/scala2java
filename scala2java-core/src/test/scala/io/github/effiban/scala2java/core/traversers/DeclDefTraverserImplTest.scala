package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.{Decl, Mod, Term, Type, XtensionQuasiquoteMod, XtensionQuasiquoteTermParam, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class DeclDefTraverserImplTest extends UnitTestSuite {

  private val MethodType = t"MethodType"
  private val TraversedMethodType = t"TraversedMethodType"
  private val MethodName: Term.Name = Term.Name("myMethod")

  private val ScalaMods: List[Mod] = List(mod"@MyAnnotation")
  private val TraversedScalaMods: List[Mod] = List(mod"@MyTraversedAnnotation")

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
  private val typeTraverser = mock[TypeTraverser]
  private val typeParamTraverser = mock[TypeParamTraverser]
  private val termParamTraverser = mock[TermParamTraverser]

  private val declDefTraverser = new DeclDefTraverserImpl(
    statModListTraverser,
    typeParamTraverser,
    typeTraverser,
    termParamTraverser
  )


  test("traverse() for class method when has one list of params") {
    val declDef = Decl.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = MethodType
    )

    val expectedTraversedDeclDef = Decl.Def(
      mods = TraversedScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(TraversedMethodParamList1),
      decltpe = TraversedMethodType
    )

    doReturn(TraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(ScalaMods))
    doReturn(TraversedMethodType).when(typeTraverser).traverse(eqTree(MethodType))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
      case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))

    declDefTraverser.traverse(declDef).structure shouldBe expectedTraversedDeclDef.structure
  }

  test("traverse() for class method when has type params") {
    val declDef = Decl.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = TypeParams,
      paramss = List(MethodParamList1),
      decltpe = MethodType
    )

    val expectedTraversedDeclDef = Decl.Def(
      mods = TraversedScalaMods,
      name = MethodName,
      tparams = TraversedTypeParams,
      paramss = List(TraversedMethodParamList1),
      decltpe = TraversedMethodType
    )

    doReturn(TraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(ScalaMods))
    doAnswer((tparam: Type.Param) => tparam match {
      case aTypeParam if aTypeParam.structure == TypeParam1.structure => TraversedTypeParam1
      case aTypeParam if aTypeParam.structure == TypeParam2.structure => TraversedTypeParam2
      case aTypeParam => aTypeParam
    }).when(typeParamTraverser).traverse(any[Type.Param])
    doReturn(TraversedMethodType).when(typeTraverser).traverse(eqTree(MethodType))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
      case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))

    declDefTraverser.traverse(declDef).structure shouldBe expectedTraversedDeclDef.structure
  }

  test("traverse() for interface method when has one list of params") {
    val declDef = Decl.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1),
      decltpe = MethodType
    )

    val expectedTraversedDeclDef = Decl.Def(
      mods = TraversedScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(TraversedMethodParamList1),
      decltpe = TraversedMethodType
    )

    doReturn(TraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(ScalaMods))
    doReturn(TraversedMethodType).when(typeTraverser).traverse(eqTree(MethodType))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
      case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))

    declDefTraverser.traverse(declDef).structure shouldBe expectedTraversedDeclDef.structure
  }

  test("traverse() for interface method when has two lists of params") {
    val declDef = Decl.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(MethodParamList1, MethodParamList2),
      decltpe = MethodType
    )

    val expectedTraversedDeclDef = Decl.Def(
      mods = TraversedScalaMods,
      name = MethodName,
      tparams = Nil,
      paramss = List(TraversedMethodParamList1, TraversedMethodParamList2),
      decltpe = TraversedMethodType
    )

    doReturn(TraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(ScalaMods))
    doReturn(TraversedMethodType).when(typeTraverser).traverse(eqTree(MethodType))
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == MethodParam1.structure => TraversedMethodParam1
      case aParam if aParam.structure == MethodParam2.structure => TraversedMethodParam2
      case aParam if aParam.structure == MethodParam3.structure => TraversedMethodParam3
      case aParam if aParam.structure == MethodParam4.structure => TraversedMethodParam4
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))

    declDefTraverser.traverse(declDef).structure shouldBe expectedTraversedDeclDef.structure
  }
}
