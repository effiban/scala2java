package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.ParameterizedInitializerNameTypeMapping
import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqOptionTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteTerm}

class InitializerDeclDefInferrerImplTest extends UnitTestSuite {

  private val compositeCollectiveTypeInferrer = mock[CompositeCollectiveTypeInferrer]
  private val parameterizedInitializerNameTypeMapping= mock[ParameterizedInitializerNameTypeMapping]

  private val initializerDeclDefInferrer = new InitializerDeclDefInferrerImpl(
    compositeCollectiveTypeInferrer,
    parameterizedInitializerNameTypeMapping
  )

  test("inferByAppliedTypes() when inferrable") {
    val termName = TermNames.Map
    val parameterizedType = TypeNames.Map
    val appliedTypes = List(TypeNames.String, TypeNames.Int)
    val numArgs = 2

    when(parameterizedInitializerNameTypeMapping.typeInitializedBy(eqTree(termName))).thenReturn(Some(parameterizedType))

    initializerDeclDefInferrer.inferByAppliedTypes(termName, appliedTypes, numArgs) should equalPartialDeclDef(
      PartialDeclDef(
        maybeParamTypes = List.fill(numArgs)(Some(Type.Tuple(appliedTypes))),
        maybeReturnType = Some(Type.Apply(parameterizedType, appliedTypes))
      )
    )
  }

  test("inferByAppliedTypes() when not inferrable") {
    val termName = q"blabla"

    when(parameterizedInitializerNameTypeMapping.typeInitializedBy(eqTree(termName))).thenReturn(None)

    initializerDeclDefInferrer.inferByAppliedTypes(termName, List(TypeNames.Int), 2) should equalPartialDeclDef(PartialDeclDef())
  }

  test("inferByArgTypes() for tuple arg type when inferrable") {
    val termName = TermNames.Map
    val parameterizedType = TypeNames.Map
    val tupleArgType = Type.Tuple(List(TypeNames.String, TypeNames.Int))
    val tupleArgTypes = List.fill(2)(tupleArgType)
    val maybeTupleArgTypes = tupleArgTypes.map(Some(_))

    when(parameterizedInitializerNameTypeMapping.typeInitializedBy(eqTree(termName))).thenReturn(Some(parameterizedType))
    when(compositeCollectiveTypeInferrer.infer(eqOptionTreeList(maybeTupleArgTypes))).thenReturn(tupleArgType)

    initializerDeclDefInferrer.inferByArgTypes(termName, maybeTupleArgTypes) should equalPartialDeclDef(
      PartialDeclDef(
        maybeParamTypes = maybeTupleArgTypes,
        maybeReturnType = Some(Type.Apply(parameterizedType, tupleArgType.args))
      )
    )
  }

  test("inferByArgTypes() for scalar arg type when inferrable") {
    val termName = TermNames.List
    val parameterizedType = TypeNames.List
    val argType = TypeNames.String
    val argTypes = List.fill(2)(argType)
    val maybeArgTypes = argTypes.map(Some(_))

    when(parameterizedInitializerNameTypeMapping.typeInitializedBy(eqTree(termName))).thenReturn(Some(parameterizedType))
    when(compositeCollectiveTypeInferrer.infer(eqOptionTreeList(maybeArgTypes))).thenReturn(argType)

    initializerDeclDefInferrer.inferByArgTypes(termName, maybeArgTypes) should equalPartialDeclDef(
      PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(Type.Apply(parameterizedType, List(argType)))
      )
    )
  }

  test("inferByArgTypes() when not inferrable") {
    val termName = q"blabla"

    when(parameterizedInitializerNameTypeMapping.typeInitializedBy(eqTree(termName))).thenReturn(None)

    initializerDeclDefInferrer.inferByArgTypes(termName, List(Some(TypeNames.Int))) should equalPartialDeclDef(PartialDeclDef())
  }
}
