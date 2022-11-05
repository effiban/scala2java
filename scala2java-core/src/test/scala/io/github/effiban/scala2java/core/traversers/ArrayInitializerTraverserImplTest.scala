package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArrayInitializerSizeContext, ArrayInitializerValuesContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.CurlyBrace
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.typeinference.ScalarArgListTypeInferrer
import org.mockito.ArgumentMatchers

import scala.meta.Lit

class ArrayInitializerTraverserImplTest extends UnitTestSuite {

  private val ExpectedListTraversalOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(CurlyBrace), traverseEmpty = true)

  private val typeTraverser = mock[TypeTraverser]
  private val termTraverser = mock[TermTraverser]
  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val scalarArgListTypeInferrer = mock[ScalarArgListTypeInferrer]

  private val arrayInitializerTraverser = new ArrayInitializerTraverserImpl(
    typeTraverser,
    termTraverser,
    argumentListTraverser,
    scalarArgListTypeInferrer
  )

  test("traverseWithValues() when has type and values") {
    val tpe = TypeNames.String
    val values = List(Lit.String("a"), Lit.String("b"))
    val context = ArrayInitializerValuesContext(maybeType = Some(tpe), values = values)

    doWrite("String").when(typeTraverser).traverse(eqTree(tpe))
    doWrite("""{ "a", "b" }""").when(argumentListTraverser).traverse(
      eqTreeList(values),
      ArgumentMatchers.eq(termTraverser),
      ArgumentMatchers.eq(ExpectedListTraversalOptions)
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
      ArgumentMatchers.eq(termTraverser),
      ArgumentMatchers.eq(ExpectedListTraversalOptions)
    )

    arrayInitializerTraverser.traverseWithValues(context)

    outputWriter.toString shouldBe "new String[] {}"
  }

  test("traverseWithValues() when has no type but has values") {
    val values = List(Lit.String("a"), Lit.String("b"))
    val context = ArrayInitializerValuesContext(values = values)

    when(scalarArgListTypeInferrer.infer(eqTreeList(values))).thenReturn(TypeNames.String)
    doWrite("String").when(typeTraverser).traverse(eqTree(TypeNames.String))
    doWrite("""{ "a", "b" }""").when(argumentListTraverser).traverse(
      eqTreeList(values),
      ArgumentMatchers.eq(termTraverser),
      ArgumentMatchers.eq(ExpectedListTraversalOptions)
    )

    arrayInitializerTraverser.traverseWithValues(context)

    outputWriter.toString shouldBe """new String[] { "a", "b" }"""
  }

  test("traverseWithValues() when has an empty context should use the type 'Any'") {
    doWrite("Object").when(typeTraverser).traverse(eqTree(TypeNames.ScalaAny))
    doWrite("""{}""").when(argumentListTraverser).traverse(
      eqTreeList(Nil),
      ArgumentMatchers.eq(termTraverser),
      ArgumentMatchers.eq(ExpectedListTraversalOptions)
    )

    arrayInitializerTraverser.traverseWithValues(ArrayInitializerValuesContext())

    outputWriter.toString shouldBe "new Object[] {}"
  }

  test("traverseWithSize() when has non-default type and non-default size") {
    val size = Lit.Int(3)
    val context = ArrayInitializerSizeContext(tpe = TypeNames.String, size = size)

    doWrite("String").when(typeTraverser).traverse(eqTree(TypeNames.String))
    doWrite("3").when(termTraverser).traverse(eqTree(size))

    arrayInitializerTraverser.traverseWithSize(context)

    outputWriter.toString shouldBe "new String[3]"
  }

  test("traverseWithSize() when has the defaults for both type and size") {
    val size = Lit.Int(0)

    doWrite("Object").when(typeTraverser).traverse(eqTree(TypeNames.ScalaAny))
    doWrite("0").when(termTraverser).traverse(eqTree(size))

    arrayInitializerTraverser.traverseWithSize(ArrayInitializerSizeContext())

    outputWriter.toString shouldBe "new Object[0]"

  }
}
