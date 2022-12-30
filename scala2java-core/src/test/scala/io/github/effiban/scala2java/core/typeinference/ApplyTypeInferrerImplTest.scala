package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.TypeNameClassifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}
import io.github.effiban.scala2java.spi.typeinferrers.ApplyTypeTypeInferrer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Term, Type}

class ApplyTypeInferrerImplTest extends UnitTestSuite {

  private val applyTypeTypeInferrer = mock[ApplyTypeTypeInferrer]
  private val termTypeInferrer = mock[TermTypeInferrer]
  private val compositeArgListTypesInferrer = mock[CompositeArgListTypesInferrer]
  private val typeNameClassifier = mock[TypeNameClassifier]

  private val applyTypeInferrer = new ApplyTypeInferrerImpl(
    applyTypeTypeInferrer,
    termTypeInferrer,
    compositeArgListTypesInferrer,
    typeNameClassifier
  )

  test("infer() when 'fun' is a explicit parameterized type ('Term.ApplyType')") {
    val stringListApplyType = Term.ApplyType(TermNames.List, List(TypeNames.String))
    // List[String]("a", "b)
    val stringListInitializer = Term.Apply(stringListApplyType, List(Lit.String("a"), Lit.String("b")))

    val expectedTypeApply = Type.Apply(TypeNames.List, List(TypeNames.String))

    when(applyTypeTypeInferrer.infer(eqTree(stringListApplyType))).thenReturn(Some(expectedTypeApply))

    applyTypeInferrer.infer(stringListInitializer).value.structure shouldBe expectedTypeApply.structure
  }

  test("infer() when 'fun' is an implicit parameterized type") {

    val mapElements = List(
      Term.Tuple(List(Lit.String("a"), Lit.Int(1))),
      Term.Tuple(List(Lit.String("b"), Lit.Int(2)))
    )
    // Map("a" -> 1, "b" -> 2)
    val mapInitializer = Term.Apply(TermNames.Map, mapElements)

    val expectedTypeApply = Type.Apply(TypeNames.Map, List(TypeNames.String, TypeNames.Int))

    when(termTypeInferrer.infer(eqTree(TermNames.Map))).thenReturn(Some(TypeNames.Map))
    when(typeNameClassifier.isParameterizedType(eqTree(TypeNames.Map))).thenReturn(true)
    when(compositeArgListTypesInferrer.infer(eqTreeList(mapElements))).thenReturn(List(TypeNames.String, TypeNames.Int))

    applyTypeInferrer.infer(mapInitializer).value.structure shouldBe expectedTypeApply.structure
  }

  test("infer() when 'fun' is a non-parametrized type name") {

    val stringTermApply = Term.Apply(TermNames.String, List(Term.Name("a")))

    when(termTypeInferrer.infer(eqTree(TermNames.String))).thenReturn(Some(TypeNames.String))
    when(typeNameClassifier.isParameterizedType(eqTree(TypeNames.Map))).thenReturn(false)

    applyTypeInferrer.infer(stringTermApply).value.structure shouldBe TypeNames.String.structure
  }

  test("infer() when 'fun' is a Type.Select") {

    val termSelect = Term.Select(Term.Name("A"), Term.Name("b"))
    val termApply = Term.Apply(termSelect, Nil)

    val expectedTypeSelect = Type.Select(Term.Name("A"), Type.Name("B"))

    when(termTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(expectedTypeSelect))

    applyTypeInferrer.infer(termApply).value.structure shouldBe expectedTypeSelect.structure
  }

  test("infer() when 'fun' is not inferrable") {

    val nonInferrableFun = Term.Name("blabla")
    val nonInferrableTermApply = Term.Apply(nonInferrableFun, Nil)

    when(termTypeInferrer.infer(eqTree(nonInferrableFun))).thenReturn(None)

    applyTypeInferrer.infer(nonInferrableTermApply) shouldBe None
  }
}
