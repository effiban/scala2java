package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TypeSelectTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TreeTransformerImplTest extends UnitTestSuite {

  private val Pkg =
    q"""
    package pkg1 {
       case class Class1(x: X)
       case class Class2(y: Y)
    }
    """

  private val TransformedPkg =
    q"""
    package pkg1 {
       case class Class1(x: XX)
       case class Class2(y: YY)
    }
    """

  private val Template =
    template"""
    A with B with C {
       val x: scala.Int
    }
    """

  private val TransformedTemplate =
    template"""
    A with B {
       val x: scala.Int
    }
    """

  private val pkgTransformer = mock[PkgTransformer]
  private val templateTransformer = mock[TemplateTransformer]
  private val internalTermApplyInfixTransformer = mock[InternalTermApplyInfixTransformer]
  private val internalTermApplyTransformer = mock[InternalTermApplyTransformer]
  private val internalTermSelectTransformer = mock[InternalTermSelectTransformer]
  private val termTupleToTermApplyTransformer = mock[TermTupleToTermApplyTransformer]
  private val functionTypeTransformer = mock[FunctionTypeTransformer]
  private val typeSelectTransformer = mock[TypeSelectTransformer]
  private val typeTupleToTypeApplyTransformer = mock[TypeTupleToTypeApplyTransformer]

  private val treeTransformer = new TreeTransformerImpl(
    pkgTransformer,
    templateTransformer,
    internalTermApplyInfixTransformer,
    internalTermApplyTransformer,
    internalTermSelectTransformer,
    termTupleToTermApplyTransformer,
    functionTypeTransformer,
    typeSelectTransformer,
    typeTupleToTypeApplyTransformer
  )

  test("transform Pkg") {
    doAnswer(TransformedPkg).when(pkgTransformer).transform(eqTree(Pkg))

    treeTransformer.transform(Pkg).structure shouldBe TransformedPkg.structure
  }

  test("transform Template") {
    doAnswer(TransformedTemplate).when(templateTransformer).transform(eqTree(Template))

    treeTransformer.transform(Template).structure shouldBe TransformedTemplate.structure
  }

  test("transform 'import' should return the same") {
    val `import` = q"import a.b.c"
    treeTransformer.transform(`import`).structure shouldBe `import`.structure
  }

  test("transform Term.ApplyInfix should return result of inner transformer") {
    val termApplyInfix = q"a fun b"
    val transformedTerm = q"fun(a, b)"

    when(internalTermApplyInfixTransformer.transform(eqTree(termApplyInfix))).thenReturn(transformedTerm)

    treeTransformer.transform(termApplyInfix).structure shouldBe transformedTerm.structure
  }

  test("transform Term.Apply should return result of inner transformer") {
    val termApply = q"foo(2)"
    val transformedTermApply = q"bar(3)"

    when(internalTermApplyTransformer.transform(eqTree(termApply))).thenReturn(transformedTermApply)

    treeTransformer.transform(termApply).structure shouldBe transformedTermApply.structure
  }

  test("transform Term.Select should return result of inner transformer") {
    val termSelect = q"a.b.c"
    val transformedTermSelect = q"a.b.cc"

    when(internalTermSelectTransformer.transform(eqTree(termSelect))).thenReturn(transformedTermSelect)

    treeTransformer.transform(termSelect).structure shouldBe transformedTermSelect.structure
  }

  test("transform Term.Tuple should return result of inner transformer") {
    val termTuple = q"(a, b, c)"
    val termApply = q"org.jooq.lambda.tuple.Tuple.tuple(a, b, c)"

    when(termTupleToTermApplyTransformer.transform(eqTree(termTuple))).thenReturn(termApply)

    treeTransformer.transform(termTuple).structure shouldBe termApply.structure
  }

  test("transform Type.Function should return result of inner transformer") {
    val typeFunction = t"(T1, T2) => T3"
    val transformedTypeFunction = t"(U1, U2) => U3"

    when(functionTypeTransformer.transform(eqTree(typeFunction))).thenReturn(transformedTypeFunction)

    treeTransformer.transform(typeFunction).structure shouldBe transformedTypeFunction.structure
  }

  test("transform Type.Select when inner transformer returns a result should return it") {
    val typeSelect = t"a.b.C"
    val transformedTypeSelect = t"a.b.CC"

    when(typeSelectTransformer.transform(eqTree(typeSelect))).thenReturn(Some(transformedTypeSelect))

    treeTransformer.transform(typeSelect).structure shouldBe transformedTypeSelect.structure
  }

  test("transform Type.Select when inner transformer returns None should transform the qualifier") {
    val typeSelect = t"a.b.C"
    val transformedTypeSelect = t"aa.bb.C"

    val qualifier = q"a.b"
    val transformedQualifier = q"aa.bb"

    when(typeSelectTransformer.transform(eqTree(typeSelect))).thenReturn(None)
    when(internalTermSelectTransformer.transform(eqTree(qualifier))).thenReturn(transformedQualifier)

    treeTransformer.transform(typeSelect).structure shouldBe transformedTypeSelect.structure
  }

  test("transform Type.Tuple should return result of inner transformer") {
    val typeTuple = t"(A, B, C)"
    val typeApply = t"org.jooq.lambda.tuple.Tuple3[A, B, C]"

    when(typeTupleToTypeApplyTransformer.transform(eqTree(typeTuple))).thenReturn(typeApply)

    treeTransformer.transform(typeTuple).structure shouldBe typeApply.structure
  }
}
