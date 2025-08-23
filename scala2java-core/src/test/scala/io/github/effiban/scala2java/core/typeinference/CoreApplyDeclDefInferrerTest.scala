package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.TypeClassifier
import io.github.effiban.scala2java.core.entities.{TermSelects, TypeSelects}
import io.github.effiban.scala2java.core.matchers.PartialDeclDefScalatestMatcher.equalPartialDeclDef
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
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

  test("""infer scala.collection.immutable.List.apply[java.lang.String]("a", "b")""") {
    val termApply = q"""scala.collection.immutable.List.apply[java.lang.String]("a", "b")"""

    val argTypes = List(TypeSelects.JavaString, TypeSelects.JavaString)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypeLists = List(maybeArgTypes))

    val expectedReturnType = Type.Apply(TypeSelects.ScalaList, List(TypeSelects.JavaString))

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermSelects.ScalaList), eqTreeList(List(TypeSelects.JavaString)), eqTo(2)))
      .thenReturn(PartialDeclDef(
        maybeParamTypeLists = List(List(Some(TypeSelects.JavaString), Some(TypeSelects.JavaString))),
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypeLists = List(maybeArgTypes), maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer scala.collection.immutable.Map.apply[java.lang.String, scala.Int](("a", 1), ("b", 2))""") {
    val termApply = q"""scala.collection.immutable.Map.apply[java.lang.String, scala.Int](("a", 1), ("b", 2))"""

    val appliedTypes = List(TypeSelects.JavaString, TypeSelects.ScalaInt)
    val argTypes = List.fill(2)(Type.Tuple(appliedTypes))
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypeLists = List(maybeArgTypes))

    val expectedReturnType = Type.Apply(TypeSelects.ScalaMap, appliedTypes)

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermSelects.ScalaMap), eqTreeList(appliedTypes), eqTo(2)))
      .thenReturn(PartialDeclDef(
        maybeParamTypeLists = List(maybeArgTypes),
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypeLists = List(maybeArgTypes), maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer scala.collection.immutable.Map.empty[java.lang.String, scala.Int]()""") {
    val termApply = q"""scala.collection.immutable.Map.empty[java.lang.String, scala.Int]()"""

    val appliedTypes = List(TypeSelects.JavaString, TypeSelects.ScalaInt)
    val context = TermApplyInferenceContext()

    val expectedReturnType = Type.Apply(TypeSelects.ScalaMap, appliedTypes)

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermSelects.ScalaMap), eqTreeList(appliedTypes), eqTo(0)))
      .thenReturn(PartialDeclDef(
        maybeParamTypeLists = Nil,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypeLists = Nil, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer scala.concurrent.Future.successful[scala.Int](1)""") {
    val termApply = q"""scala.concurrent.Future.successful[scala.Int](1)"""

    val appliedTypes = List(TypeSelects.ScalaInt)
    val argTypes = List(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypeLists = List(maybeArgTypes))

    val expectedReturnType = Type.Apply(TypeSelects.ScalaFuture, appliedTypes)

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermSelects.ScalaFuture), eqTreeList(appliedTypes), eqTo(1)))
      .thenReturn(PartialDeclDef(
        maybeParamTypeLists = List(maybeArgTypes),
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypeLists = List(maybeArgTypes), maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer scala.concurrent.Future.failed[scala.Int](new RuntimeException())") {
    val termApply = q"scala.concurrent.Future.failed[scala.Int](new RuntimeException())"

    val appliedTypes = List(TypeSelects.ScalaInt)
    val argTypes = List(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypeLists = List(maybeArgTypes))

    val expectedReturnType = Type.Apply(TypeSelects.ScalaFuture, appliedTypes)

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypeLists = List(maybeArgTypes), maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer scala.collection.immutable.List.apply("a", "b")""") {
    val termApply = q"""scala.collection.immutable.List.apply("a", "b")"""

    val argTypes = List(TypeSelects.JavaString, TypeSelects.JavaString)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypeLists = List(maybeArgTypes))

    val expectedReturnType = Type.Apply(TypeSelects.ScalaList, List(TypeSelects.JavaString))

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermSelects.ScalaList), eqOptionTreeList(maybeArgTypes)))
      .thenReturn(PartialDeclDef(
        maybeParamTypeLists = List(maybeArgTypes),
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypeLists = List(maybeArgTypes), maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer scala.collection.immutable.Map.apply(("a", 1), ("b", 2))""") {
    val termApply = q"""scala.collection.immutable.Map.apply(("a", 1), ("b", 2))"""

    val tupleTypes = List(TypeSelects.JavaString, TypeSelects.ScalaInt)
    val argTypes = List.fill(2)(Type.Tuple(tupleTypes))
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypeLists = List(maybeArgTypes))

    val expectedReturnType = Type.Apply(TypeSelects.ScalaMap, tupleTypes)

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermSelects.ScalaMap), eqOptionTreeList(maybeArgTypes)))
      .thenReturn(PartialDeclDef(
        maybeParamTypeLists = List(maybeArgTypes),
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypeLists = List(maybeArgTypes), maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer scala.collection.immutable.Map.empty()") {
    val termApply = q"scala.collection.immutable.Map.empty()"

    val context = TermApplyInferenceContext()

    val expectedReturnType = Type.Apply(TypeSelects.ScalaMap, List(TypeSelects.ScalaAny))

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermSelects.ScalaMap), eqTo(Nil)))
      .thenReturn(PartialDeclDef(
        maybeParamTypeLists = Nil,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypeLists = Nil, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer scala.collection.immutable.Range.inclusive(1, 10)") {
    val termApply = q"scala.collection.immutable.Range.inclusive(1, 10)"

    val argTypes = List.fill(2)(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypeLists = List(maybeArgTypes))

    val expectedReturnType = Type.Apply(TypeSelects.ScalaRange, List(TypeSelects.ScalaInt))

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermSelects.ScalaRange), eqOptionTreeList(maybeArgTypes)))
      .thenReturn(PartialDeclDef(
        maybeParamTypeLists = List(maybeArgTypes),
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypeLists = List(maybeArgTypes), maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer scala.concurrent.Future.successful(1)") {
    val termApply = q"scala.concurrent.Future.successful(1)"

    val argTypes = List(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypeLists = List(maybeArgTypes))

    val expectedReturnType = Type.Apply(TypeSelects.ScalaFuture, List(TypeSelects.ScalaInt))

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermSelects.ScalaFuture), eqOptionTreeList(maybeArgTypes)))
      .thenReturn(PartialDeclDef(
        maybeParamTypeLists = List(maybeArgTypes),
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypeLists = List(maybeArgTypes), maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer scala.concurrent.Future.failed(new RuntimeException())") {
    val termApply = q"scala.concurrent.Future.failed(new RuntimeException())"

    val argTypes = List(t"RuntimeException")
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypeLists = List(maybeArgTypes))

    val expectedReturnType = Type.Apply(TypeSelects.ScalaFuture, List(TypeSelects.ScalaAny))

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypeLists = List(maybeArgTypes), maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer scala.collection.immutable.List(1, 2).take(1)") {
    val termApply = q"scala.collection.immutable.List(1, 2).take(1)"

    val parentType = Type.Apply(TypeSelects.ScalaList, List(TypeSelects.ScalaInt))
    val argTypes = List(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeParentType = Some(parentType), maybeArgTypeLists = List(maybeArgTypes))

    val expectedReturnType = parentType

    when(typeClassifier.isJavaListLike(eqTree(parentType))).thenReturn(true)

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypeLists = List(maybeArgTypes), maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer foo.take(1) when the type of 'foo' is not a list") {
    val termApply = q"foo.take(1)"

    val parentType = t"Foo"
    val argTypes = List(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeParentType = Some(parentType), maybeArgTypeLists = List(maybeArgTypes))

    when(typeClassifier.isJavaListLike(eqTree(parentType))).thenReturn(false)

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypeLists = List(maybeArgTypes))
    )
  }

  test("infer scala.collection.immutable.List(1, 2).length()") {
    val termApply = q"scala.collection.immutable.List(1, 2).length()"

    val parentType = Type.Apply(TypeSelects.ScalaList, List(TypeSelects.ScalaInt))
    val context = TermApplyInferenceContext(maybeParentType = Some(parentType))
    val expectedReturnType = TypeSelects.ScalaInt

    when(typeClassifier.isJavaListLike(eqTree(parentType))).thenReturn(true)

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer scala.Predef.print("abc"")""") {
    coreApplyDeclDefInferrer.infer(q"""scala.Predef.print("abc")""", TermApplyInferenceContext()) should equalPartialDeclDef(
      PartialDeclDef(maybeReturnType = Some(TypeSelects.ScalaUnit))
    )
  }

  test("""infer scala.Predef.println("abc"")""") {
    coreApplyDeclDefInferrer.infer(q"""scala.Predef.println("abc")""", TermApplyInferenceContext()) should equalPartialDeclDef(
      PartialDeclDef(maybeReturnType = Some(TypeSelects.ScalaUnit))
    )
  }

  test("infer x.toString()") {
    coreApplyDeclDefInferrer.infer(q"x.toString()", TermApplyInferenceContext()) should equalPartialDeclDef(
      PartialDeclDef(maybeReturnType = Some(TypeSelects.JavaString))
    )
  }

  test("infer() when 'fun' is not inferrable") {
    coreApplyDeclDefInferrer.infer(q"blabla()", TermApplyInferenceContext()) should equalPartialDeclDef(PartialDeclDef())
  }
}
