package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TypeSelectTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

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

  private val pkgTransformer = mock[PkgTransformer]
  private val internalTermApplyTransformer = mock[InternalTermApplyTransformer]
  private val internalTermSelectTransformer = mock[InternalTermSelectTransformer]
  private val typeSelectTransformer = mock[TypeSelectTransformer]

  private val treeTransformer = new TreeTransformerImpl(
    pkgTransformer,
    internalTermApplyTransformer,
    internalTermSelectTransformer,
    typeSelectTransformer
  )

  test("transform Pkg") {
    doAnswer(TransformedPkg).when(pkgTransformer).transform(eqTree(Pkg))

    treeTransformer.transform(Pkg).structure shouldBe TransformedPkg.structure
  }

  test("transform 'import' should return the same") {
    val `import` = q"import a.b.c"
    treeTransformer.transform(`import`).structure shouldBe `import`.structure
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
}
