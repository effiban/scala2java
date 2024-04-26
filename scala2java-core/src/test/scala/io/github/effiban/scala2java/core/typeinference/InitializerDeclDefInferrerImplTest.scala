package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.{ParameterizedInitializerNameTypeMapping, TermSelects, TypeSelects}
import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqOptionTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteTerm}

class InitializerDeclDefInferrerImplTest extends UnitTestSuite {

  private val compositeCollectiveTypeInferrer = mock[CompositeCollectiveTypeInferrer]
  private val parameterizedInitializerNameTypeMapping = mock[ParameterizedInitializerNameTypeMapping]

  private val initializerDeclDefInferrer = new InitializerDeclDefInferrerImpl(
    compositeCollectiveTypeInferrer,
    parameterizedInitializerNameTypeMapping
  )

  test("inferByAppliedTypes() when inferrable") {
    val termSelect = TermSelects.ScalaMap
    val parameterizedType = TypeSelects.ScalaMap
    val appliedTypes = List(TypeSelects.ScalaString, TypeSelects.ScalaInt)
    val numArgs = 2

    when(parameterizedInitializerNameTypeMapping.typeInitializedBy(eqTree(termSelect))).thenReturn(Some(parameterizedType))

    initializerDeclDefInferrer.inferByAppliedTypes(termSelect, appliedTypes, numArgs) should equalPartialDeclDef(
      PartialDeclDef(
        maybeParamTypes = List.fill(numArgs)(Some(Type.Tuple(appliedTypes))),
        maybeReturnType = Some(Type.Apply(parameterizedType, appliedTypes))
      )
    )
  }

  test("inferByAppliedTypes() when not inferrable") {
    val termSelect = q"blabla.gaga"

    when(parameterizedInitializerNameTypeMapping.typeInitializedBy(eqTree(termSelect))).thenReturn(None)

    initializerDeclDefInferrer.inferByAppliedTypes(termSelect, List(TypeSelects.ScalaInt), 2) should equalPartialDeclDef(PartialDeclDef())
  }

  test("inferByArgTypes() for tuple arg type when inferrable") {
    val termSelect = TermSelects.ScalaMap
    val parameterizedType = TypeSelects.ScalaMap
    val tupleArgType = Type.Tuple(List(TypeSelects.ScalaString, TypeSelects.ScalaInt))
    val tupleArgTypes = List.fill(2)(tupleArgType)
    val maybeTupleArgTypes = tupleArgTypes.map(Some(_))

    when(parameterizedInitializerNameTypeMapping.typeInitializedBy(eqTree(termSelect))).thenReturn(Some(parameterizedType))
    when(compositeCollectiveTypeInferrer.infer(eqOptionTreeList(maybeTupleArgTypes))).thenReturn(tupleArgType)

    initializerDeclDefInferrer.inferByArgTypes(termSelect, maybeTupleArgTypes) should equalPartialDeclDef(
      PartialDeclDef(
        maybeParamTypes = maybeTupleArgTypes,
        maybeReturnType = Some(Type.Apply(parameterizedType, tupleArgType.args))
      )
    )
  }

  test("inferByArgTypes() for scalar arg type when inferrable") {
    val termSelect = TermSelects.ScalaList
    val parameterizedType = TypeSelects.ScalaList
    val argType = TypeSelects.ScalaString
    val argTypes = List.fill(2)(argType)
    val maybeArgTypes = argTypes.map(Some(_))

    when(parameterizedInitializerNameTypeMapping.typeInitializedBy(eqTree(termSelect))).thenReturn(Some(parameterizedType))
    when(compositeCollectiveTypeInferrer.infer(eqOptionTreeList(maybeArgTypes))).thenReturn(argType)

    initializerDeclDefInferrer.inferByArgTypes(termSelect, maybeArgTypes) should equalPartialDeclDef(
      PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(Type.Apply(parameterizedType, List(argType)))
      )
    )
  }

  test("inferByArgTypes() when not inferrable") {
    val termSelect = q"blabla.gaga"

    when(parameterizedInitializerNameTypeMapping.typeInitializedBy(eqTree(termSelect))).thenReturn(None)

    initializerDeclDefInferrer.inferByArgTypes(termSelect, List(Some(TypeNames.Int))) should equalPartialDeclDef(PartialDeclDef())
  }
}
