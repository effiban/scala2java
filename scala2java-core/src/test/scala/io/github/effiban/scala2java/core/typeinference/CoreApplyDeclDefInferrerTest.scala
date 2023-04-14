package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.TypeClassifier
import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.spi.entities.PartialDeclDef
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.{eqOptionTreeList, eqTreeList}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Type, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class CoreApplyDeclDefInferrerTest extends UnitTestSuite {

  private val initializerDeclDefInferrer = mock[InitializerDeclDefInferrer]
  private val typeClassifier = mock[TypeClassifier[Type]]

  private val coreApplyDeclDefInferrer = new CoreApplyDeclDefInferrer(initializerDeclDefInferrer, typeClassifier)

  test("""infer List.apply[String]("a", "b")""") {
    val termApply = q"""List.apply[String]("a", "b")"""

    val argTypes = List(TypeNames.String, TypeNames.String)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeNames.List, List(TypeNames.String))

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermNames.List), eqTreeList(List(TypeNames.String)), eqTo(2)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = List(Some(TypeNames.String), Some(TypeNames.String)),
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer Map.apply[String, Int](("a", 1), ("b", 2))""") {
    val termApply = q"""Map.apply[String, Int](("a", 1), ("b", 2))"""

    val appliedTypes = List(TypeNames.String, TypeNames.Int)
    val argTypes = List.fill(2)(Type.Tuple(appliedTypes))
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeNames.Map, appliedTypes)

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermNames.Map), eqTreeList(appliedTypes), eqTo(2)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer Map.empty[String, Int]()""") {
    val termApply = q"""Map.empty[String, Int]()"""

    val appliedTypes = List(TypeNames.String, TypeNames.Int)
    val context = TermApplyInferenceContext(maybeArgTypes = Nil)

    val expectedReturnType = Type.Apply(TypeNames.Map, appliedTypes)

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermNames.Map), eqTreeList(appliedTypes), eqTo(0)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = Nil,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = Nil, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer Range.inclusive[Int](1, 10)""") {
    val termApply = q"""Range.inclusive[Int](1, 10)"""

    val appliedTypes = List(TypeNames.Int)
    val argTypes = List.fill(2)(TypeNames.Int)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeNames.ScalaRange, appliedTypes)

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermNames.ScalaRange), eqTreeList(appliedTypes), eqTo(2)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer Future.successful[Int](1)""") {
    val termApply = q"""Future.successful[Int](1)"""

    val appliedTypes = List(TypeNames.Int)
    val argTypes = List(TypeNames.Int)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeNames.Future, appliedTypes)

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermNames.Future), eqTreeList(appliedTypes), eqTo(1)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer Future.failed[Int](new RuntimeException())") {
    val termApply = q"Future.failed[Int](new RuntimeException())"

    val appliedTypes = List(TypeNames.Int)
    val argTypes = List(TypeNames.Int)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeNames.Future, appliedTypes)

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer List.apply("a", "b")""") {
    val termApply = q"""List.apply("a", "b")"""

    val argTypes = List(TypeNames.String, TypeNames.String)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeNames.List, List(TypeNames.String))

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermNames.List), eqOptionTreeList(maybeArgTypes)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer Map.apply(("a", 1), ("b", 2))""") {
    val termApply = q"""Map.apply(("a", 1), ("b", 2))"""

    val tupleTypes = List(TypeNames.String, TypeNames.Int)
    val argTypes = List.fill(2)(Type.Tuple(tupleTypes))
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeNames.Map, tupleTypes)

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermNames.Map), eqOptionTreeList(maybeArgTypes)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer Map.empty()") {
    val termApply = q"Map.empty()"

    val context = TermApplyInferenceContext()

    val expectedReturnType = Type.Apply(TypeNames.Map, List(TypeNames.ScalaAny))

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermNames.Map), eqTo(Nil)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = Nil,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = Nil, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer Range.inclusive(1, 10)") {
    val termApply = q"Range.inclusive(1, 10)"

    val argTypes = List.fill(2)(TypeNames.Int)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeNames.ScalaRange, List(TypeNames.Int))

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermNames.ScalaRange), eqOptionTreeList(maybeArgTypes)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer Future.successful(1)") {
    val termApply = q"Future.successful(1)"

    val argTypes = List(TypeNames.Int)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeNames.Future, List(TypeNames.Int))

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermNames.Future), eqOptionTreeList(maybeArgTypes)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer Future.failed(new RuntimeException())") {
    val termApply = q"Future.failed(new RuntimeException())"

    val argTypes = List(t"RuntimeException")
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeNames.Future, List(TypeNames.ScalaAny))

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer List(1, 2).take(1)") {
    val termApply = q"List(1, 2).take(1)"

    val parentType = Type.Apply(TypeNames.List, List(TypeNames.Int))
    val argTypes = List(TypeNames.Int)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeParentType = Some(parentType), maybeArgTypes = maybeArgTypes)

    val expectedReturnType = parentType

    when(typeClassifier.isJavaListLike(eqTree(parentType))).thenReturn(true)

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer foo.take(1) when the type of 'foo' is not a list") {
    val termApply = q"foo.take(1)"

    val parentType = t"Foo"
    val argTypes = List(TypeNames.Int)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeParentType = Some(parentType), maybeArgTypes = maybeArgTypes)

    when(typeClassifier.isJavaListLike(eqTree(parentType))).thenReturn(false)

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes)
    )
  }

  test("infer List(1, 2).length()") {
    val termApply = q"List(1, 2).length()"

    val parentType = Type.Apply(TypeNames.List, List(TypeNames.Int))
    val context = TermApplyInferenceContext(maybeParentType = Some(parentType))
    val expectedReturnType = TypeNames.Int

    when(typeClassifier.isJavaListLike(eqTree(parentType))).thenReturn(true)

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer x.toString()") {
    coreApplyDeclDefInferrer.infer(q"x.toString()", TermApplyInferenceContext()) should equalPartialDeclDef(
      PartialDeclDef(maybeReturnType = Some(TypeNames.String))
    )
  }

  test("""infer print("abc"")""") {
    coreApplyDeclDefInferrer.infer(q"""print("abc")""", TermApplyInferenceContext()) should equalPartialDeclDef(
      PartialDeclDef(maybeReturnType = Some(TypeNames.Unit))
    )
  }

  test("""infer println("abc"")""") {
    coreApplyDeclDefInferrer.infer(q"""println("abc")""", TermApplyInferenceContext()) should equalPartialDeclDef(
      PartialDeclDef(maybeReturnType = Some(TypeNames.Unit))
    )
  }

  test("infer() when 'fun' is not inferrable") {
    coreApplyDeclDefInferrer.infer(q"blabla()", TermApplyInferenceContext()) should equalPartialDeclDef(PartialDeclDef())
  }
}
