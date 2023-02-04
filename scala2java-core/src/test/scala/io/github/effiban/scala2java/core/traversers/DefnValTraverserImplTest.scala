package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.matchers.ModifiersContextMatcher.eqModifiersContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.transformers.{DefnValToDeclVarTransformer, DefnValTransformer}
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.{eqSomeTree, eqTreeList}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.{Defn, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DefnValTraverserImplTest extends UnitTestSuite {

  private val modListTraverser = mock[ModListTraverser]
  private val defnValOrVarTypeTraverser = mock[DefnValOrVarTypeTraverser]
  private val patListTraverser = mock[PatListTraverser]
  private val expressionTraverser = mock[ExpressionTraverser]
  private val declVarTraverser = mock[DeclVarTraverser]
  private val defnValToDeclVarTransformer = mock[DefnValToDeclVarTransformer]
  private val defnValTransformer = mock[DefnValTransformer]

  private val defnValTraverser = new DefnValTraverserImpl(
    modListTraverser,
    defnValOrVarTypeTraverser,
    patListTraverser,
    expressionTraverser,
    declVarTraverser,
    defnValToDeclVarTransformer,
    defnValTransformer
  )


  test("traverse() when transformed to a Decl.Var, should traverse with the DeclVarTraverser") {
    val javaScope = JavaScope.Class

    val defnVal = q"val myMock = mock[Foo]"
    val declVar = q"@Mock var myVal: Foo"

    val context = StatContext(javaScope)

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(Some(declVar))

    defnValTraverser.traverse(defnVal, context)

    verify(declVarTraverser).traverse(eqTree(declVar), ArgumentMatchers.eq(context))
  }

  test("traverse() when transformed to another Defn.Val, and it is a class member - typed") {
    val javaScope = JavaScope.Class

    val defnVal = q"@MyAnnotation val myVal: Int = 3"
    val defnVal2 = q"@MyAnnotation val myVal2: Int = 3"

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(None)
    when(defnValTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(defnVal2)
    doWrite(
      """@MyAnnotation
        |private final """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVal2, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(eqSomeTree(t"Int"), eqSomeTree(q"3"), ArgumentMatchers.eq(StatContext(javaScope)))
    doWrite("myVal2").when(patListTraverser).traverse(eqTreeList(List(p"myVal2")))
    doWrite("3").when(expressionTraverser).traverse(eqTree(q"3"))

    defnValTraverser.traverse(defnVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final int myVal2 = 3""".stripMargin
  }

  test("traverse() when transformed to another Defn.Val, and it is a class member - untyped") {
    val javaScope = JavaScope.Class

    val defnVal = q"@MyAnnotation val myVal = 3"
    val defnVal2 = q"@MyAnnotation val myVal2 = 3"

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(None)
    when(defnValTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(defnVal2)
    doWrite(
      """@MyAnnotation
        |private final """.stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVal2, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(ArgumentMatchers.eq(None), eqSomeTree(q"3"), ArgumentMatchers.eq(StatContext(javaScope)))
    doWrite("myVal2").when(patListTraverser).traverse(eqTreeList(List(p"myVal2")))
    doWrite("3").when(expressionTraverser).traverse(eqTree(q"3"))

    defnValTraverser.traverse(defnVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |private final int myVal2 = 3""".stripMargin
  }

  test("traverse() when transformed to another Defn.Val, and it is an interface member - typed") {
    val javaScope = JavaScope.Interface

    val defnVal = q"@MyAnnotation val myVal: Int = 3"
    val defnVal2 = q"@MyAnnotation val myVal2: Int = 3"

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(None)
    when(defnValTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(defnVal2)
    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVal2, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(eqSomeTree(t"Int"), eqSomeTree(q"3"), ArgumentMatchers.eq(StatContext(javaScope)))
    doWrite("myVal2").when(patListTraverser).traverse(eqTreeList(List(p"myVal2")))
    doWrite("3").when(expressionTraverser).traverse(eqTree(q"3"))

    defnValTraverser.traverse(defnVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVal2 = 3""".stripMargin
  }

  test("traverse() when transformed to another Defn.Val, and it is an interface member - untyped") {
    val javaScope = JavaScope.Interface

    val defnVal = q"@MyAnnotation val myVal = 3"
    val defnVal2 = q"@MyAnnotation val myVal2 = 3"

    when(defnValToDeclVarTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(None)
    when(defnValTransformer.transform(eqTree(defnVal), ArgumentMatchers.eq(javaScope))).thenReturn(defnVal2)
    doWrite(
      """@MyAnnotation
        |""".stripMargin)
      .when(modListTraverser).traverse(eqExpectedModifiers(defnVal2, javaScope), annotsOnSameLine = ArgumentMatchers.eq(false))
    doWrite("int")
      .when(defnValOrVarTypeTraverser).traverse(ArgumentMatchers.eq(None), eqSomeTree(q"3"), ArgumentMatchers.eq(StatContext(javaScope)))
    doWrite("myVal2").when(patListTraverser).traverse(eqTreeList(List(p"myVal2")))
    doWrite("3").when(expressionTraverser).traverse(eqTree(q"3"))

    defnValTraverser.traverse(defnVal, StatContext(javaScope))

    outputWriter.toString shouldBe
      """@MyAnnotation
        |int myVal2 = 3""".stripMargin
  }

  private def eqExpectedModifiers(defnVal: Defn.Val, javaScope: JavaScope) = {
    val expectedModifiersContext = ModifiersContext(defnVal, JavaTreeType.Variable, javaScope)
    eqModifiersContext(expectedModifiersContext)
  }
}
