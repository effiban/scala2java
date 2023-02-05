package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, ArrayInitializerSizeContext, ArrayInitializerValuesContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.CurlyBrace
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.typeinference.{CompositeCollectiveTypeInferrer, TermTypeInferrer}
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.{eqOptionTreeList, eqTreeList}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.captor.ArgCaptor

import scala.meta.{Lit, Term}

class ArrayInitializerTraverserImplTest extends UnitTestSuite {

  private val ExpectedListTraversalOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(CurlyBrace), traverseEmpty = true)
  private val ExpectedArgListContext = ArgumentListContext(options = ExpectedListTraversalOptions)

  private val typeTraverser = mock[TypeTraverser]
  private val expressionTraverser = mock[ExpressionTraverser]
  private val termArgumentTraverser = mock[ArgumentTraverser[Term]]
  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val termTypeInferrer = mock[TermTypeInferrer]
  private val compositeCollectiveTypeInferrer = mock[CompositeCollectiveTypeInferrer]

  private val argumentTraverserCaptor = ArgCaptor[ArgumentTraverser[Term]]

  private val arrayInitializerTraverser = new ArrayInitializerTraverserImpl(
    typeTraverser,
    expressionTraverser,
    termArgumentTraverser,
    argumentListTraverser,
    termTypeInferrer,
    compositeCollectiveTypeInferrer
  )

  test("traverseWithValues() when has type and values") {
    val tpe = TypeNames.String
    val values = List(Lit.String("a"), Lit.String("b"))
    val context = ArrayInitializerValuesContext(maybeType = Some(tpe), values = values)

    doWrite("String").when(typeTraverser).traverse(eqTree(tpe))
    doWrite("""{ "a", "b" }""").when(argumentListTraverser).traverse(
      eqTreeList(values),
      eqTo(termArgumentTraverser),
      eqArgumentListContext(ExpectedArgListContext)
    )

    arrayInitializerTraverser.traverseWithValues(context)

    outputWriter.toString shouldBe """new String[] { "a", "b" }"""
  }

  test("traverseWithValues() when has type and no values") {
    val tpe = TypeNames.String
    val context = ArrayInitializerValuesContext(maybeType = Some(tpe))

    doWrite("String").when(typeTraverser).traverse(eqTree(tpe))
    doWrite("""{}""").when(argumentListTraverser).traverse(
      ArgumentMatchers.eq(Nil),
      eqTo(termArgumentTraverser),
      eqArgumentListContext(ExpectedArgListContext)
    )

    arrayInitializerTraverser.traverseWithValues(context)

    outputWriter.toString shouldBe "new String[] {}"
  }

  test("traverseWithValues() when has no type but has values") {
    val values = List(Lit.String("a"), Lit.String("b"))
    val maybeTypes = List(Some(TypeNames.String), Some(TypeNames.String))
    val context = ArrayInitializerValuesContext(values = values)

    values.foreach(value => when(termTypeInferrer.infer(value)).thenReturn(Some(TypeNames.String)))
    when(compositeCollectiveTypeInferrer.infer(eqOptionTreeList(maybeTypes))).thenReturn(TypeNames.String)
    doWrite("String").when(typeTraverser).traverse(eqTree(TypeNames.String))
    doWrite("""{ "a", "b" }""").when(argumentListTraverser).traverse(
      eqTreeList(values),
      eqTo(termArgumentTraverser),
      eqArgumentListContext(ExpectedArgListContext)
    )

    arrayInitializerTraverser.traverseWithValues(context)

    outputWriter.toString shouldBe """new String[] { "a", "b" }"""
  }

  test("traverseWithValues() when has an empty context should use the type 'Any'") {
    doWrite("Object").when(typeTraverser).traverse(eqTree(TypeNames.ScalaAny))
    doWrite("""{}""").when(argumentListTraverser).traverse(
      eqTreeList(Nil),
      eqTo(termArgumentTraverser),
      eqArgumentListContext(ExpectedArgListContext)
    )

    arrayInitializerTraverser.traverseWithValues(ArrayInitializerValuesContext())

    outputWriter.toString shouldBe "new Object[] {}"
  }

  test("traverseWithSize() when has non-default type and non-default size") {
    val size = Lit.Int(3)
    val context = ArrayInitializerSizeContext(tpe = TypeNames.String, size = size)

    doWrite("String").when(typeTraverser).traverse(eqTree(TypeNames.String))
    doWrite("3").when(expressionTraverser).traverse(eqTree(size))

    arrayInitializerTraverser.traverseWithSize(context)

    outputWriter.toString shouldBe "new String[3]"
  }

  test("traverseWithSize() when has the defaults for both type and size") {
    val size = Lit.Int(0)

    doWrite("Object").when(typeTraverser).traverse(eqTree(TypeNames.ScalaAny))
    doWrite("0").when(expressionTraverser).traverse(eqTree(size))

    arrayInitializerTraverser.traverseWithSize(ArrayInitializerSizeContext())

    outputWriter.toString shouldBe "new Object[0]"

  }
}
