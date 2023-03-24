package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.TypeNameClassifier
import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.spi.typeinferrers.ApplyTypeTypeInferrer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqOptionTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Term, Type, XtensionQuasiquoteTerm}

class CoreApplyDeclDefInferrerTest extends UnitTestSuite {

  private val applyTypeTypeInferrer = mock[ApplyTypeTypeInferrer]
  private val termTypeInferrer = mock[TermTypeInferrer]
  private val compositeCollectiveTypeInferrer = mock[CompositeCollectiveTypeInferrer]
  private val typeNameClassifier = mock[TypeNameClassifier]

  private val coreApplyDeclDefInferrer = new CoreApplyDeclDefInferrer(
    applyTypeTypeInferrer,
    termTypeInferrer,
    compositeCollectiveTypeInferrer,
    typeNameClassifier
  )

  test("infer() when 'fun' is a explicit parameterized type ('Term.ApplyType')") {
    val stringListApplyType = Term.ApplyType(TermNames.List, List(TypeNames.String))
    // List[String]("a", "b)
    val stringListInitializer = Term.Apply(stringListApplyType, List(Lit.String("a"), Lit.String("b")))

    val argTypes = List(TypeNames.String)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedTypeApply = Type.Apply(TypeNames.List, List(TypeNames.String))

    when(applyTypeTypeInferrer.infer(eqTree(stringListApplyType))).thenReturn(Some(expectedTypeApply))

    coreApplyDeclDefInferrer.infer(stringListInitializer, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedTypeApply))
    )
  }

  test("infer() when 'fun' is a List with implicit type") {

    // List("a", "b)
    val listInitializer = Term.Apply(TermNames.List, List(q"a", q"b"))

    val argTypes = List(TypeNames.String)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedTypeApply = Type.Apply(TypeNames.List, argTypes)

    when(termTypeInferrer.infer(eqTree(TermNames.List))).thenReturn(Some(TypeNames.List))
    when(typeNameClassifier.isParameterizedType(eqTree(TypeNames.List))).thenReturn(true)
    when(compositeCollectiveTypeInferrer.infer(eqOptionTreeList(maybeArgTypes))).thenReturn(TypeNames.String)

    coreApplyDeclDefInferrer.infer(listInitializer, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedTypeApply))
    )
  }

  test("infer() when 'fun' is a Map with implicit types") {

    val mapElements = List(
      Term.Tuple(List(Lit.String("a"), Lit.Int(1))),
      Term.Tuple(List(Lit.String("b"), Lit.Int(2)))
    )

    // Map(("a", 1), ("b", 2))
    val mapInitializer = Term.Apply(TermNames.Map, mapElements)

    val tupleType = Type.Tuple(List(TypeNames.String, TypeNames.Int))
    val argTypes = List(tupleType, tupleType)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedTypeApply = Type.Apply(TypeNames.Map, tupleType.args)

    when(termTypeInferrer.infer(eqTree(TermNames.Map))).thenReturn(Some(TypeNames.Map))
    when(typeNameClassifier.isParameterizedType(eqTree(TypeNames.Map))).thenReturn(true)
    when(compositeCollectiveTypeInferrer.infer(eqOptionTreeList(maybeArgTypes))).thenReturn(tupleType)

    coreApplyDeclDefInferrer.infer(mapInitializer, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedTypeApply))
    )
  }

  test("infer() when 'fun' is a non-parametrized type name") {

    val stringTermApply = Term.Apply(TermNames.String, List(Term.Name("a")))
    val maybeArgTypes = List(Some(TypeNames.String))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    when(termTypeInferrer.infer(eqTree(TermNames.String))).thenReturn(Some(TypeNames.String))
    when(typeNameClassifier.isParameterizedType(eqTree(TypeNames.Map))).thenReturn(false)

    coreApplyDeclDefInferrer.infer(stringTermApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(TypeNames.String))
    )
  }

  test("infer() when 'fun' is a Type.Select and inferrable") {

    val termSelect = Term.Select(Term.Name("A"), Term.Name("b"))
    val termApply = Term.Apply(termSelect, Nil)
    val maybeArgTypes = List(Some(TypeNames.String), Some(TypeNames.String))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedTypeSelect = Type.Select(Term.Name("A"), Type.Name("B"))

    when(termTypeInferrer.infer(eqTree(termSelect))).thenReturn(Some(expectedTypeSelect))

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedTypeSelect))
    )
  }

  test("infer() when 'fun' is not inferrable") {

    val nonInferrableFun = Term.Name("blabla")
    val nonInferrableTermApply = Term.Apply(nonInferrableFun, Nil)
    val maybeArgTypes = List(Some(TypeNames.String))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    when(termTypeInferrer.infer(eqTree(nonInferrableFun))).thenReturn(None)

    coreApplyDeclDefInferrer.infer(nonInferrableTermApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes)
    )
  }
}
