package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.Decision.{Uncertain, Yes}
import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaUnit
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.TermTypeInferrer
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.transformers.DefnDefTransformer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.Term.Block
import scala.meta.{Defn, Term, Type, XtensionQuasiquoteMod, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class DefnDefTraverserImplTest extends UnitTestSuite {

  private val MethodName = q"myMethod"
  private val TransformedMethodName = q"myTransformedMethod"

  private val TheAnnot = mod"@MyAnnotation"
  private val TheTransformedAnnot = mod"@MyTransformedAnnotation"
  private val TheTraversedAnnot = mod"@MyTraversedAnnotation"

  private val ScalaMods = List(TheAnnot)
  private val TransformedScalaMods = List(TheTransformedAnnot)
  private val TraversedScalaMods = List(TheTraversedAnnot)

  private val TypeParam1 = tparam"T1"
  private val TypeParam2 = tparam"T2"
  private val TypeParams = List(TypeParam1, TypeParam2)

  private val TransformedTypeParam1 = tparam"T11"
  private val TransformedTypeParam2 = tparam"T22"
  private val TransformedTypeParams = List(TransformedTypeParam1, TransformedTypeParam2)

  private val TraversedTypeParam1 = tparam"T111"
  private val TraversedTypeParam2 = tparam"T222"
  private val TraversedTypeParams = List(TraversedTypeParam1, TraversedTypeParam2)

  private val MethodParam1 = param"param1: Int"
  private val MethodParam2 = param"param2: Int"
  private val MethodParam3 = param"param3: Int"
  private val MethodParam4 = param"param4: Int"

  private val MethodParamList1 = List(MethodParam1, MethodParam2)
  private val MethodParamList2 = List(MethodParam3, MethodParam4)

  private val TransformedMethodParam1 = param"param1: Int"
  private val TransformedMethodParam2 = param"param2: Int"
  private val TransformedMethodParam3 = param"param3: Int"
  private val TransformedMethodParam4 = param"param4: Int"

  private val TransformedMethodParamList1 = List(MethodParam1, MethodParam2)
  private val TransformedMethodParamList2 = List(MethodParam3, MethodParam4)

  private val TraversedMethodParam1 = param"param11: Int"
  private val TraversedMethodParam2 = param"param22: Int"
  private val TraversedMethodParam3 = param"param33: Int"
  private val TraversedMethodParam4 = param"param44: Int"

  private val TraversedMethodParamList1 = List(TraversedMethodParam1, TraversedMethodParam2)
  private val TraversedMethodParamList2 = List(TraversedMethodParam3, TraversedMethodParam4)

  private val Statement1 = q"doSomething(param1)"
  private val Statement2 = q"doSomethingElse(param2)"

  private val TransformedStatement1 = q"doSomething(param11)"
  private val TransformedStatement2 = q"doSomethingElse(param22)"

  private val TraversedStatement1 = q"doSomething(param111)"
  private val TraversedStatement2 = q"doSomethingElse(param222)"


  private val statModListTraverser = mock[StatModListTraverser]
  private val typeParamTraverser = mock[TypeParamTraverser]
  private val typeTraverser = mock[TypeTraverser]
  private val termParamTraverser = mock[TermParamTraverser]
  private val blockWrappingTermTraverser = mock[BlockWrappingTermTraverser]
  private val termTypeInferrer = mock[TermTypeInferrer]
  private val defnDefTransformer = mock[DefnDefTransformer]

  private val defnDefTraverser = new DefnDefTraverserImpl(
    statModListTraverser,
    typeParamTraverser,
    typeTraverser,
    termParamTraverser,
    blockWrappingTermTraverser,
    termTypeInferrer,
    defnDefTransformer
  )


  test("traverse() for method with one statement returning non-Unit") {
    val methodType = t"MyType"
    val transformedMethodType = t"MyTransformedType"
    val traversedMethodType = t"MyTraversedType"

    val initialDefnDef = initialDefnDefWith(
      paramss = List(MethodParamList1),
      maybeDeclType = Some(methodType),
      body = Statement1
    )

    val transformedDefnDef = transformedDefnDefWith(
      paramss = List(TransformedMethodParamList1),
      maybeDeclType = Some(transformedMethodType),
      body = TransformedStatement1
    )

    val expectedTraversedBody = Block(List(TraversedStatement1))
    val expectedTraversedDefnDef = traversedDefnDefWith(
      paramss = List(TraversedMethodParamList1),
      maybeDeclType = Some(traversedMethodType),
      body = expectedTraversedBody
    )

    when(defnDefTransformer.transform(eqTree(initialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(TraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TransformedScalaMods))
    doReturn(traversedMethodType).when(typeTraverser).traverse(eqTree(transformedMethodType))
    expectTraverseOneParamList()
    doReturn(expectedTraversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(TransformedStatement1),
        context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
      )

    defnDefTraverser.traverse(initialDefnDef).structure shouldBe expectedTraversedDefnDef.structure
  }

  test("traverse() for method with one statement returning Unit") {
    val initialDefnDef = initialDefnDefWith(
      paramss = List(MethodParamList1),
      maybeDeclType = Some(ScalaUnit),
      body = Statement1
    )

    val transformedDefnDef = transformedDefnDefWith(
      paramss = List(TransformedMethodParamList1),
      maybeDeclType = Some(ScalaUnit),
      body = TransformedStatement1
    )

    val expectedTraversedBody = Block(List(TraversedStatement1))
    val expectedTraversedDefnDef = traversedDefnDefWith(
      paramss = List(TraversedMethodParamList1),
      maybeDeclType = Some(t"void"),
      body = expectedTraversedBody
    )

    when(defnDefTransformer.transform(eqTree(initialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(TraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TransformedScalaMods))
    doReturn(t"void").when(typeTraverser).traverse(eqTree(ScalaUnit))
    expectTraverseOneParamList()
    doReturn(expectedTraversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(TransformedStatement1),
        context = eqBlockContext(BlockContext())
      )

    defnDefTraverser.traverse(initialDefnDef).structure shouldBe expectedTraversedDefnDef.structure
  }

  test("traverse() for method with type params") {
    val initialDefnDef = initialDefnDefWith(
      tparams = TypeParams,
      paramss = List(MethodParamList1),
      maybeDeclType = Some(ScalaUnit),
      body = Statement1
    )

    val transformedDefnDef = transformedDefnDefWith(
      tparams = TransformedTypeParams,
      paramss = List(TransformedMethodParamList1),
      maybeDeclType = Some(ScalaUnit),
      body = TransformedStatement1
    )

    val expectedTraversedBody = Block(List(TraversedStatement1))
    val expectedTraversedDefnDef = traversedDefnDefWith(
      tparams = TraversedTypeParams,
      paramss = List(TraversedMethodParamList1),
      maybeDeclType = Some(t"void"),
      body = expectedTraversedBody
    )

    when(defnDefTransformer.transform(eqTree(initialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(TraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TransformedScalaMods))
    doAnswer((tparam: Type.Param) => tparam match {
      case aTypeParam if aTypeParam.structure == TransformedTypeParam1.structure => TraversedTypeParam1
      case aTypeParam if aTypeParam.structure == TransformedTypeParam2.structure => TraversedTypeParam2
      case aTypeParam => aTypeParam
    }).when(typeParamTraverser).traverse(any[Type.Param])
    doReturn(t"void").when(typeTraverser).traverse(eqTree(ScalaUnit))
    expectTraverseOneParamList()
    doReturn(expectedTraversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(TransformedStatement1),
        context = eqBlockContext(BlockContext())
      )

    defnDefTraverser.traverse(initialDefnDef).structure shouldBe expectedTraversedDefnDef.structure
  }


  test("traverse() for method with one statement missing return type when not inferrable") {
    val initialDefnDef = initialDefnDefWith(
      paramss = List(MethodParamList1),
      body = Statement1
    )

    val transformedDefnDef = transformedDefnDefWith(
      paramss = List(TransformedMethodParamList1),
      body = TransformedStatement1
    )

    val expectedTraversedBody = Block(List(TraversedStatement1))
    val expectedTraversedDefnDef = traversedDefnDefWith(
      paramss = List(TraversedMethodParamList1),
      body = expectedTraversedBody
    )

    when(defnDefTransformer.transform(eqTree(initialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(TraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TransformedScalaMods))
    when(termTypeInferrer.infer(eqTree(TransformedStatement1))).thenReturn(None)
    expectTraverseOneParamList()
    doReturn(expectedTraversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(TransformedStatement1),
        context = eqBlockContext(BlockContext(shouldReturnValue = Uncertain))
      )

    defnDefTraverser.traverse(initialDefnDef).structure shouldBe expectedTraversedDefnDef.structure
  }

  test("traverse() for method with one statement missing return type when inferrable") {
    val inferredMethodType = t"MyInferredType"
    val traversedMethodType = t"MyTraversedType"

    val initialDefnDef = initialDefnDefWith(
      paramss = List(MethodParamList1),
      body = Statement1
    )

    val transformedDefnDef = transformedDefnDefWith(
      paramss = List(TransformedMethodParamList1),
      body = TransformedStatement1
    )

    val expectedTraversedBody = Block(List(TraversedStatement1))
    val expectedTraversedDefnDef = traversedDefnDefWith(
      paramss = List(TraversedMethodParamList1),
      maybeDeclType = Some(traversedMethodType),
      body = expectedTraversedBody
    )

    when(defnDefTransformer.transform(eqTree(initialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(TraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TransformedScalaMods))
    when(termTypeInferrer.infer(eqTree(TransformedStatement1))).thenReturn(Some(inferredMethodType))
    doReturn(traversedMethodType).when(typeTraverser).traverse(eqTree(inferredMethodType))
    expectTraverseOneParamList()
    doReturn(expectedTraversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(TransformedStatement1),
        context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
      )

    defnDefTraverser.traverse(initialDefnDef).structure shouldBe expectedTraversedDefnDef.structure
  }

  test("traverse() for method with block") {
    val methodType = t"MyType"
    val transformedMethodType = t"MyTransformedType"
    val traversedMethodType = t"MyTraversedType"

    val initialDefnDef = initialDefnDefWith(
      paramss = List(MethodParamList1),
      maybeDeclType = Some(methodType),
      body = Block(List(Statement1, Statement2))
    )

    val expectedTransformedBody = Block(List(TransformedStatement1, TransformedStatement2))
    val transformedDefnDef = transformedDefnDefWith(
      paramss = List(TransformedMethodParamList1),
      maybeDeclType = Some(transformedMethodType),
      body = expectedTransformedBody
    )

    val expectedTraversedBody = Block(List(TraversedStatement1, TraversedStatement2))
    val expectedTraversedDefnDef = traversedDefnDefWith(
      paramss = List(TraversedMethodParamList1),
      maybeDeclType = Some(traversedMethodType),
      body = expectedTraversedBody
    )

    when(defnDefTransformer.transform(eqTree(initialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(TraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TransformedScalaMods))
    doReturn(traversedMethodType).when(typeTraverser).traverse(eqTree(transformedMethodType))
    expectTraverseOneParamList()
    doReturn(expectedTraversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(expectedTransformedBody),
        context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
      )

    defnDefTraverser.traverse(initialDefnDef).structure shouldBe expectedTraversedDefnDef.structure
  }

  test("traverse() for method with two lists of params") {
    val methodType = t"MyType"
    val transformedMethodType = t"MyTransformedType"
    val traversedMethodType = t"MyTraversedType"

    val initialDefnDef = initialDefnDefWith(
      paramss = List(MethodParamList1, MethodParamList2),
      maybeDeclType = Some(methodType),
      body = Statement1
    )

    val transformedDefnDef = transformedDefnDefWith(
      paramss = List(TransformedMethodParamList1, TransformedMethodParamList2),
      maybeDeclType = Some(transformedMethodType),
      body = TransformedStatement1
    )

    val expectedTraversedBody = Block(List(TraversedStatement1))
    val expectedTraversedDefnDef = traversedDefnDefWith(
      paramss = List(TraversedMethodParamList1, TraversedMethodParamList2),
      maybeDeclType = Some(traversedMethodType),
      body = expectedTraversedBody
    )

    when(defnDefTransformer.transform(eqTree(initialDefnDef))).thenReturn(transformedDefnDef)
    doReturn(TraversedScalaMods).when(statModListTraverser).traverse(eqTreeList(TransformedScalaMods))
    doReturn(traversedMethodType).when(typeTraverser).traverse(eqTree(transformedMethodType))
    expectTraverseTwoParamLists()
    doReturn(expectedTraversedBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(TransformedStatement1),
        context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
      )

    defnDefTraverser.traverse(initialDefnDef).structure shouldBe expectedTraversedDefnDef.structure
  }

  private def initialDefnDefWith(tparams: List[Type.Param] = Nil,
                                 paramss: List[List[Term.Param]] = Nil,
                                 maybeDeclType: Option[Type] = None,
                                 body: Term) = {
    Defn.Def(
      mods = ScalaMods,
      name = MethodName,
      tparams = tparams,
      paramss = paramss,
      decltpe = maybeDeclType,
      body = body
    )
  }

  private def transformedDefnDefWith(tparams: List[Type.Param] = Nil,
                                     paramss: List[List[Term.Param]] = Nil,
                                     maybeDeclType: Option[Type] = None,
                                     body: Term) = {
    Defn.Def(
      mods = TransformedScalaMods,
      name = TransformedMethodName,
      tparams = tparams,
      paramss = paramss,
      decltpe = maybeDeclType,
      body = body
    )
  }

  private def traversedDefnDefWith(tparams: List[Type.Param] = Nil,
                                   paramss: List[List[Term.Param]] = Nil,
                                   maybeDeclType: Option[Type.Name] = None,
                                   body: Term) = {
    Defn.Def(
      mods = TraversedScalaMods,
      name = TransformedMethodName,
      tparams = tparams,
      paramss = paramss,
      decltpe = maybeDeclType,
      body = body
    )
  }

  private def expectTraverseOneParamList(): Unit = {
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == TransformedMethodParam1.structure => TraversedMethodParam1
      case aParam if aParam.structure == TransformedMethodParam2.structure => TraversedMethodParam2
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
  }

  private def expectTraverseTwoParamLists(): Unit = {
    doAnswer((param: Term.Param) => param match {
      case aParam if aParam.structure == TransformedMethodParam1.structure => TraversedMethodParam1
      case aParam if aParam.structure == TransformedMethodParam2.structure => TraversedMethodParam2
      case aParam if aParam.structure == TransformedMethodParam3.structure => TraversedMethodParam3
      case aParam if aParam.structure == TransformedMethodParam4.structure => TraversedMethodParam4
      case aParam => aParam
    }).when(termParamTraverser).traverse(any[Term.Param], eqTo(StatContext(JavaScope.MethodSignature)))
  }
}
