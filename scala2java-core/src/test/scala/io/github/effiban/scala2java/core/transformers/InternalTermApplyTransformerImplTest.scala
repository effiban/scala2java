package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TermNames.Apply
import io.github.effiban.scala2java.core.factories.TermApplyTransformationContextFactory
import io.github.effiban.scala2java.core.matchers.QualifiedTermApplyMockitoMatcher.eqQualifiedTermApply
import io.github.effiban.scala2java.core.matchers.TermApplyTransformationContextMockitoMatcher.eqTermApplyTransformationContext
import io.github.effiban.scala2java.core.matchers.UnqualifiedTermApplyMockitoMatcher.eqUnqualifiedTermApply
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.spi.entities.{QualifiedTermApply, UnqualifiedTermApply}
import io.github.effiban.scala2java.spi.transformers.{QualifiedTermApplyTransformer, UnqualifiedTermApplyTransformer}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Tree, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InternalTermApplyTransformerImplTest extends UnitTestSuite {

  private val Context = TermApplyTransformationContext(maybeQualifierType = Some(t"Parent"))

  private val treeTransformer = mock[TreeTransformer]
  private val qualifiedTransformer = mock[QualifiedTermApplyTransformer]
  private val unqualifiedTransformer = mock[UnqualifiedTermApplyTransformer]
  private val termSelectTermFunctionTransformer = mock[TermSelectTermFunctionTransformer]
  private val transformationContextFactory = mock[TermApplyTransformationContextFactory]

  private val internalTermApplyTransformer = new InternalTermApplyTransformerImpl(
    treeTransformer,
    qualifiedTransformer,
    unqualifiedTransformer,
    termSelectTermFunctionTransformer,
    transformationContextFactory
  )


  test("transform() of a qualified method invocation with no type, when qualified transformer returns a value") {
    val termApply = q"myQual.myMethod(1, 2)"
    val qualifiedTermApply = QualifiedTermApply(q"myQual.myMethod", List(q"11", q"22"))
    val javaQualifiedTermApply = QualifiedTermApply(q"myJavaQual.myJavaMethod", List(q"11", q"22"))
    val javaTermApply = q"myJavaQual.myJavaMethod(11, 22)"

    when(transformationContextFactory.create(eqTree(termApply))).thenReturn(Context)

    doAnswer((tree: Tree) => tree match {
      case q"1" => q"11"
      case q"2" => q"22"
      case other => other
    }).when(treeTransformer).transform(any[Tree])

    when(qualifiedTransformer.transform(
      eqQualifiedTermApply(qualifiedTermApply),
      eqTermApplyTransformationContext(Context))
    ).thenReturn(Some(javaQualifiedTermApply))

    internalTermApplyTransformer.transform(termApply).structure shouldBe javaTermApply.structure
  }

  test("transform() of a qualified method invocation with types, when qualified transformer returns a value") {
    val termApply = q"""myQual.myMethod[scala.String, scala.Int]("a", 1)"""
    val qualifiedTermApply = QualifiedTermApply(
      q"myQual.myMethod", List(t"java.lang.String", t"java.lang.Integer"), List(q""""aa"""", q"11")
    )
    val javaQualifiedTermApply = QualifiedTermApply(
      q"myJavaQual.myJavaMethod", List(t"java.lang.String", t"java.lang.Integer"), List(q""""aa"""", q"11")
    )
    val javaTermApply = q"""myJavaQual.myJavaMethod[java.lang.String, java.lang.Integer]("aa", 11)"""


    when(transformationContextFactory.create(eqTree(termApply))).thenReturn(Context)

    doAnswer((tree: Tree) => tree match {
      case q""""a"""" => q""""aa""""
      case q"1" => q"11"
      case t"scala.String" => t"java.lang.String"
      case t"scala.Int" => t"java.lang.Integer"
      case other => other
    }).when(treeTransformer).transform(any[Tree])

    when(qualifiedTransformer.transform(
      eqQualifiedTermApply(qualifiedTermApply),
      eqTermApplyTransformationContext(Context))
    ).thenReturn(Some(javaQualifiedTermApply))

    internalTermApplyTransformer.transform(termApply).structure shouldBe javaTermApply.structure
  }

  test("transform() of a qualified method invocation with no type, " +
    "when qualified transformer returns None and unqualified returns a value") {

    val termApply = q"myQual.myMethod(1)"
    val qualifiedTermApply = QualifiedTermApply(q"myQual.myMethod", List(q"11"))
    val javaUnqualifiedTermApply = UnqualifiedTermApply(q"myJavaMethod", List(q"11"))
    val javaTermApply = q"myJavaQual.myJavaMethod(11)"

    when(transformationContextFactory.create(eqTree(termApply))).thenReturn(Context)

    doAnswer((tree: Tree) => tree match {
      case q"1" => q"11"
      case q"myQual" => q"myJavaQual"
      case other => other
    }).when(treeTransformer).transform(any[Tree])

    when(qualifiedTransformer.transform(
      eqQualifiedTermApply(qualifiedTermApply),
      eqTermApplyTransformationContext(Context))
    ).thenReturn(None)

    when(unqualifiedTransformer.transform(
      eqUnqualifiedTermApply(qualifiedTermApply.asUnqualified()),
      eqTermApplyTransformationContext(Context))
    ).thenReturn(Some(javaUnqualifiedTermApply))

    internalTermApplyTransformer.transform(termApply).structure shouldBe javaTermApply.structure
  }

  test("transform() of a qualified method invocation with a type, " +
    "when qualified transformer returns None and unqualified returns a value") {

    val termApply = q"myQual.myMethod[scala.Int](1)"
    val qualifiedTermApply = QualifiedTermApply(q"myQual.myMethod", List(t"java.lang.Integer"), List(q"11"))
    val javaUnqualifiedTermApply = UnqualifiedTermApply(q"myJavaMethod", List(t"java.lang.Integer"), List(q"11"))
    val javaTermApply = q"myJavaQual.myJavaMethod[java.lang.Integer](11)"

    when(transformationContextFactory.create(eqTree(termApply))).thenReturn(Context)

    doAnswer((tree: Tree) => tree match {
      case q"1" => q"11"
      case t"scala.Int" => t"java.lang.Integer"
      case q"myQual" => q"myJavaQual"
      case other => other
    }).when(treeTransformer).transform(any[Tree])

    when(qualifiedTransformer.transform(
      eqQualifiedTermApply(qualifiedTermApply),
      eqTermApplyTransformationContext(Context))
    ).thenReturn(None)

    when(unqualifiedTransformer.transform(
      eqUnqualifiedTermApply(qualifiedTermApply.asUnqualified()),
      eqTermApplyTransformationContext(Context))
    ).thenReturn(Some(javaUnqualifiedTermApply))

    internalTermApplyTransformer.transform(termApply).structure shouldBe javaTermApply.structure
  }

  test("transform() of a qualified method invocation with no type when both transformers return None") {

    val termApply = q"myQual.myMethod(1)"
    val qualifiedTermApply = QualifiedTermApply(q"myQual.myMethod", List(q"11"))
    val javaTermApply = q"myJavaQual.myMethod(11)"

    when(transformationContextFactory.create(eqTree(termApply))).thenReturn(Context)

    doAnswer((tree: Tree) => tree match {
      case q"1" => q"11"
      case q"myQual" => q"myJavaQual"
      case other => other
    }).when(treeTransformer).transform(any[Tree])

    when(qualifiedTransformer.transform(
      eqQualifiedTermApply(qualifiedTermApply),
      eqTermApplyTransformationContext(Context))
    ).thenReturn(None)

    when(unqualifiedTransformer.transform(
      eqUnqualifiedTermApply(qualifiedTermApply.asUnqualified()),
      eqTermApplyTransformationContext(Context))
    ).thenReturn(None)

    internalTermApplyTransformer.transform(termApply).structure shouldBe javaTermApply.structure
  }

  test("transform() of a qualified method invocation with a type, when both transformers return None") {

    val termApply = q"myQual.myMethod[scala.Int](1)"
    val qualifiedTermApply = QualifiedTermApply(q"myQual.myMethod", List(t"java.lang.Integer"), List(q"11"))
    val javaTermApply = q"myJavaQual.myMethod[java.lang.Integer](11)"

    when(transformationContextFactory.create(eqTree(termApply))).thenReturn(Context)

    doAnswer((tree: Tree) => tree match {
      case q"1" => q"11"
      case t"scala.Int" => t"java.lang.Integer"
      case q"myQual" => q"myJavaQual"
      case other => other
    }).when(treeTransformer).transform(any[Tree])

    when(qualifiedTransformer.transform(
      eqQualifiedTermApply(qualifiedTermApply),
      eqTermApplyTransformationContext(Context))
    ).thenReturn(None)

    when(unqualifiedTransformer.transform(
      eqUnqualifiedTermApply(qualifiedTermApply.asUnqualified()),
      eqTermApplyTransformationContext(Context))
    ).thenReturn(None)

    internalTermApplyTransformer.transform(termApply).structure shouldBe javaTermApply.structure
  }

  test("transform a 'Term.Function' (lambda) with a method name, should return result of 'TermSelectTermFunctionTransformer'") {
    val lambda = q"(x: Int) => print(x)"
    val termApply = q"((x: Int) => print(x)).apply()"
    val expectedTransformedTermSelect = q"(((x: int) => print(x)): java.util.function.Consumer[java.lang.Integer]).accept"
    val expectedTransformedTermApply = q"(((x: int) => print(x)): java.util.function.Consumer[java.lang.Integer]).accept()"

    when(transformationContextFactory.create(eqTree(termApply))).thenReturn(Context)

    when(termSelectTermFunctionTransformer.transform(eqTree(lambda), eqTree(Apply))).thenReturn(expectedTransformedTermSelect)

    internalTermApplyTransformer.transform(termApply).structure shouldBe expectedTransformedTermApply.structure
  }

  test("transform() of a method invocation which is not a qualified name, should transform the children (components) separately") {
    val termApply = q"(f1(x) + f2(y))(z.w)"
    val expectedTransformedTermApply = q"(f1(xx) + f2(yy))(zz.ww)"

    when(transformationContextFactory.create(eqTree(termApply))).thenReturn(Context)

    doAnswer((tree: Tree) => tree match {
      case q"(f1(x) + f2(y))" => q"(f1(xx) + f2(yy))"
      case q"z.w" => q"zz.ww"
      case other => other
    }).when(treeTransformer).transform(any[Tree])

    internalTermApplyTransformer.transform(termApply).structure shouldBe expectedTransformedTermApply.structure
  }
}
