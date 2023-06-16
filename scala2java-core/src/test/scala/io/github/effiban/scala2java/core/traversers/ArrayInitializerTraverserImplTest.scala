package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArrayInitializerSizeContext, ArrayInitializerTypedValuesContext, ArrayInitializerValuesContext}
import io.github.effiban.scala2java.core.matchers.ArrayInitializerSizeContextScalatestMatcher.equalArrayInitializerSizeContext
import io.github.effiban.scala2java.core.matchers.ArrayInitializerTypedValuesContextScalatestMatcher.equalArrayInitializerTypedValuesContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames.ScalaAny
import io.github.effiban.scala2java.core.typeinference.{CompositeCollectiveTypeInferrer, TermTypeInferrer}
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqOptionTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Lit, Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class ArrayInitializerTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val termTypeInferrer = mock[TermTypeInferrer]
  private val compositeCollectiveTypeInferrer = mock[CompositeCollectiveTypeInferrer]

  private val arrayInitializerTraverser = new ArrayInitializerTraverserImpl(
    typeTraverser,
    expressionTermTraverser,
    termTypeInferrer,
    compositeCollectiveTypeInferrer
  )

  test("traverseWithValues() when has type and values") {
    val tpe = t"T"
    val traversedType = t"U"
    val val1 = q"val1"
    val val2 = q"val2"
    val values = List(val1, val2)
    val traversedVal1 = q"traversedVal1"
    val traversedVal2 = q"traversedVal2"
    val traversedValues = List(traversedVal1, traversedVal2)
    val context = ArrayInitializerValuesContext(maybeType = Some(tpe), values = values)
    val expectedOutputContext = ArrayInitializerTypedValuesContext(tpe = traversedType, values = traversedValues)

    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))
    doAnswer((value: Term) => value match {
      case aVal if aVal.structure == val1.structure => traversedVal1
      case aVal if aVal.structure == val2.structure => traversedVal2
      case aVal => aVal
    }).when(expressionTermTraverser).traverse(any[Term])

    arrayInitializerTraverser.traverseWithValues(context) should equalArrayInitializerTypedValuesContext(expectedOutputContext)
  }

  test("traverseWithValues() when has type and no values") {
    val tpe = t"T"
    val traversedType = t"U"
    val context = ArrayInitializerValuesContext(maybeType = Some(tpe))
    val expectedOutputContext = ArrayInitializerTypedValuesContext(tpe = traversedType)

    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))

    arrayInitializerTraverser.traverseWithValues(context) should equalArrayInitializerTypedValuesContext(expectedOutputContext)
  }

  test("traverseWithValues() when has no type but has values") {
    val tpe = t"T"
    val traversedType = t"U"
    val val1 = q"val1"
    val val2 = q"val2"
    val values = List(val1, val2)
    val traversedVal1 = q"traversedVal1"
    val traversedVal2 = q"traversedVal2"
    val traversedValues = List(traversedVal1, traversedVal2)
    val maybeTypes = List(Some(tpe), Some(tpe))
    val context = ArrayInitializerValuesContext(values = values)
    val expectedOutputContext = ArrayInitializerTypedValuesContext(tpe = traversedType, values = traversedValues)

    values.foreach(value => when(termTypeInferrer.infer(value)).thenReturn(Some(tpe)))
    when(compositeCollectiveTypeInferrer.infer(eqOptionTreeList(maybeTypes))).thenReturn(tpe)
    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))
    doAnswer((value: Term) => value match {
      case aVal if aVal.structure == val1.structure => traversedVal1
      case aVal if aVal.structure == val2.structure => traversedVal2
      case aVal => aVal
    }).when(expressionTermTraverser).traverse(any[Term])

    arrayInitializerTraverser.traverseWithValues(context) should equalArrayInitializerTypedValuesContext(expectedOutputContext)
  }

  test("traverseWithValues() when has an empty context should use the type 'Any'") {
    val expectedOutputContext = ArrayInitializerTypedValuesContext(tpe = ScalaAny)

    doReturn(ScalaAny).when(typeTraverser).traverse(eqTree(ScalaAny))

    arrayInitializerTraverser.traverseWithValues(ArrayInitializerValuesContext()) should
      equalArrayInitializerTypedValuesContext(expectedOutputContext)
  }

  test("traverseWithSize() when has non-default type and non-default size") {
    val tpe = t"T"
    val traversedType = t"U"
    val size = q"three"
    val traversedSize = Lit.Int(3)
    val context = ArrayInitializerSizeContext(tpe = tpe, size = size)
    val expectedOutputContext = ArrayInitializerSizeContext(tpe = traversedType, size = traversedSize)

    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))
    doReturn(traversedSize).when(expressionTermTraverser).traverse(eqTree(size))

    arrayInitializerTraverser.traverseWithSize(context) should equalArrayInitializerSizeContext(expectedOutputContext)
  }

  test("traverseWithSize() when has the defaults for both type and size") {
    val traversedSize = q"0.0"
    val expectedOutputContext = ArrayInitializerSizeContext(tpe = ScalaAny, size = traversedSize)

    doReturn(ScalaAny).when(typeTraverser).traverse(eqTree(ScalaAny))
    doReturn(traversedSize).when(expressionTermTraverser).traverse(eqTree(q"0"))

    arrayInitializerTraverser.traverseWithSize(ArrayInitializerSizeContext()) should
      equalArrayInitializerSizeContext(expectedOutputContext)
  }
}
