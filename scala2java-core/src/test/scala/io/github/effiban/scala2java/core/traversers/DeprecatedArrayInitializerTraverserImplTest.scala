package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, ArrayInitializerSizeContext, ArrayInitializerValuesContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.CurlyBrace
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.renderers.TypeRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames.{JavaObject, ScalaAny}
import io.github.effiban.scala2java.core.typeinference.{CompositeCollectiveTypeInferrer, TermTypeInferrer}
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.{eqOptionTreeList, eqTreeList}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Lit, Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DeprecatedArrayInitializerTraverserImplTest extends UnitTestSuite {

  private val ExpectedListTraversalOptions = ListTraversalOptions(maybeEnclosingDelimiter = Some(CurlyBrace), traverseEmpty = true)
  private val ExpectedArgListContext = ArgumentListContext(options = ExpectedListTraversalOptions)

  private val typeTraverser = mock[TypeTraverser]
  private val typeRenderer = mock[TypeRenderer]
  private val expressionTermTraverser = mock[DeprecatedExpressionTermTraverser]
  private val termArgumentTraverser = mock[DeprecatedArgumentTraverser[Term]]
  private val argumentListTraverser = mock[DeprecatedArgumentListTraverser]
  private val termTypeInferrer = mock[TermTypeInferrer]
  private val compositeCollectiveTypeInferrer = mock[CompositeCollectiveTypeInferrer]

  private val arrayInitializerTraverser = new DeprecatedArrayInitializerTraverserImpl(
    typeTraverser,
    typeRenderer,
    expressionTermTraverser,
    termArgumentTraverser,
    argumentListTraverser,
    termTypeInferrer,
    compositeCollectiveTypeInferrer
  )

  test("traverseWithValues() when has type and values") {
    val tpe = t"T"
    val traversedType = t"U"
    val values = List(q"val1", q"val2")
    val context = ArrayInitializerValuesContext(maybeType = Some(tpe), values = values)

    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))
    doWrite("U").when(typeRenderer).render(eqTree(traversedType))
    doWrite("{ val1, val2 }").when(argumentListTraverser).traverse(
      eqTreeList(values),
      eqTo(termArgumentTraverser),
      eqArgumentListContext(ExpectedArgListContext)
    )

    arrayInitializerTraverser.traverseWithValues(context)

    outputWriter.toString shouldBe "new U[] { val1, val2 }"
  }

  test("traverseWithValues() when has type and no values") {
    val tpe = t"T"
    val traversedType = t"U"
    val context = ArrayInitializerValuesContext(maybeType = Some(tpe))

    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))
    doWrite("U").when(typeRenderer).render(eqTree(traversedType))
    doWrite("""{}""").when(argumentListTraverser).traverse(
      ArgumentMatchers.eq(Nil),
      eqTo(termArgumentTraverser),
      eqArgumentListContext(ExpectedArgListContext)
    )

    arrayInitializerTraverser.traverseWithValues(context)

    outputWriter.toString shouldBe "new U[] {}"
  }

  test("traverseWithValues() when has no type but has values") {
    val tpe = t"T"
    val traversedType = t"U"
    val values = List(q"val1", q"val2")
    val maybeTypes = List(Some(tpe), Some(tpe))
    val context = ArrayInitializerValuesContext(values = values)

    values.foreach(value => when(termTypeInferrer.infer(value)).thenReturn(Some(tpe)))
    when(compositeCollectiveTypeInferrer.infer(eqOptionTreeList(maybeTypes))).thenReturn(tpe)
    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))
    doWrite("U").when(typeRenderer).render(eqTree(traversedType))
    doWrite("{ val1, val2 }").when(argumentListTraverser).traverse(
      eqTreeList(values),
      eqTo(termArgumentTraverser),
      eqArgumentListContext(ExpectedArgListContext)
    )

    arrayInitializerTraverser.traverseWithValues(context)

    outputWriter.toString shouldBe "new U[] { val1, val2 }"
  }

  test("traverseWithValues() when has an empty context should use the type 'Any'") {
    doReturn(JavaObject).when(typeTraverser).traverse(eqTree(ScalaAny))
    doWrite("Object").when(typeRenderer).render(eqTree(JavaObject))
    doWrite("""{}""").when(argumentListTraverser).traverse(
      eqTreeList(Nil),
      eqTo(termArgumentTraverser),
      eqArgumentListContext(ExpectedArgListContext)
    )

    arrayInitializerTraverser.traverseWithValues(ArrayInitializerValuesContext())

    outputWriter.toString shouldBe "new Object[] {}"
  }

  test("traverseWithSize() when has non-default type and non-default size") {
    val tpe = t"T"
    val traversedType = t"U"
    val size = Lit.Int(3)
    val context = ArrayInitializerSizeContext(tpe = tpe, size = size)

    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))
    doWrite("U").when(typeRenderer).render(eqTree(traversedType))
    doWrite("3").when(expressionTermTraverser).traverse(eqTree(size))

    arrayInitializerTraverser.traverseWithSize(context)

    outputWriter.toString shouldBe "new U[3]"
  }

  test("traverseWithSize() when has the defaults for both type and size") {
    val size = Lit.Int(0)

    doReturn(JavaObject).when(typeTraverser).traverse(eqTree(ScalaAny))
    doWrite("Object").when(typeRenderer).render(eqTree(JavaObject))
    doWrite("0").when(expressionTermTraverser).traverse(eqTree(size))

    arrayInitializerTraverser.traverseWithSize(ArrayInitializerSizeContext())

    outputWriter.toString shouldBe "new Object[0]"

  }
}
