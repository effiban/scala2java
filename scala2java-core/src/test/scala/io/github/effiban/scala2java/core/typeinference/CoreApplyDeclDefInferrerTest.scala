package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.TypeClassifier
import io.github.effiban.scala2java.core.entities.{TermSelects, TypeSelects}
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

  test("""infer scala.List.apply[scala.Predef.String]("a", "b")""") {
    val termApply = q"""scala.List.apply[scala.Predef.String]("a", "b")"""

    val argTypes = List(TypeSelects.ScalaString, TypeSelects.ScalaString)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaList, List(TypeSelects.ScalaString))

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermSelects.ScalaList), eqTreeList(List(TypeSelects.ScalaString)), eqTo(2)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = List(Some(TypeSelects.ScalaString), Some(TypeSelects.ScalaString)),
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer scala.Predef.Map.apply[scala.Predef.String, scala.Int](("a", 1), ("b", 2))""") {
    val termApply = q"""scala.Predef.Map.apply[scala.Predef.String, scala.Int](("a", 1), ("b", 2))"""

    val appliedTypes = List(TypeSelects.ScalaString, TypeSelects.ScalaInt)
    val argTypes = List.fill(2)(Type.Tuple(appliedTypes))
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaMap, appliedTypes)

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermSelects.ScalaMap), eqTreeList(appliedTypes), eqTo(2)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer scala.Predef.Map.empty[scala.Predef.String, scala.Int]()""") {
    val termApply = q"""scala.Predef.Map.empty[scala.Predef.String, scala.Int]()"""

    val appliedTypes = List(TypeSelects.ScalaString, TypeSelects.ScalaInt)
    val context = TermApplyInferenceContext(maybeArgTypes = Nil)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaMap, appliedTypes)

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermSelects.ScalaMap), eqTreeList(appliedTypes), eqTo(0)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = Nil,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = Nil, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer scala.concurrent.Future.successful[scala.Int](1)""") {
    val termApply = q"""scala.concurrent.Future.successful[scala.Int](1)"""

    val appliedTypes = List(TypeSelects.ScalaInt)
    val argTypes = List(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaFuture, appliedTypes)

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermSelects.ScalaFuture), eqTreeList(appliedTypes), eqTo(1)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer scala.concurrent.Future.failed[scala.Int](new RuntimeException())") {
    val termApply = q"scala.concurrent.Future.failed[scala.Int](new RuntimeException())"

    val appliedTypes = List(TypeSelects.ScalaInt)
    val argTypes = List(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaFuture, appliedTypes)

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer scala.List.apply("a", "b")""") {
    val termApply = q"""scala.List.apply("a", "b")"""

    val argTypes = List(TypeSelects.ScalaString, TypeSelects.ScalaString)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaList, List(TypeSelects.ScalaString))

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermSelects.ScalaList), eqOptionTreeList(maybeArgTypes)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer scala.Predef.Map.apply(("a", 1), ("b", 2))""") {
    val termApply = q"""scala.Predef.Map.apply(("a", 1), ("b", 2))"""

    val tupleTypes = List(TypeSelects.ScalaString, TypeSelects.ScalaInt)
    val argTypes = List.fill(2)(Type.Tuple(tupleTypes))
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaMap, tupleTypes)

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermSelects.ScalaMap), eqOptionTreeList(maybeArgTypes)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer scala.Predef.Map.empty()") {
    val termApply = q"scala.Predef.Map.empty()"

    val context = TermApplyInferenceContext()

    val expectedReturnType = Type.Apply(TypeSelects.ScalaMap, List(TypeNames.ScalaAny))

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermSelects.ScalaMap), eqTo(Nil)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = Nil,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = Nil, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer scala.Range.inclusive(1, 10)") {
    val termApply = q"scala.Range.inclusive(1, 10)"

    val argTypes = List.fill(2)(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeNames.ScalaRange, List(TypeSelects.ScalaInt))

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermSelects.ScalaRange), eqOptionTreeList(maybeArgTypes)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer scala.concurrent.Future.successful(1)") {
    val termApply = q"scala.concurrent.Future.successful(1)"

    val argTypes = List(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaFuture, List(TypeSelects.ScalaInt))

    when(initializerDeclDefInferrer.inferByArgTypes(eqTree(TermSelects.ScalaFuture), eqOptionTreeList(maybeArgTypes)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer scala.concurrent.Future.failed(new RuntimeException())") {
    val termApply = q"scala.concurrent.Future.failed(new RuntimeException())"

    val argTypes = List(t"RuntimeException")
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaFuture, List(TypeSelects.ScalaAny))

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer scala.List(1, 2).take(1)") {
    val termApply = q"scala.List(1, 2).take(1)"

    val parentType = Type.Apply(TypeSelects.ScalaList, List(TypeSelects.ScalaInt))
    val argTypes = List(TypeSelects.ScalaInt)
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
    val argTypes = List(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeParentType = Some(parentType), maybeArgTypes = maybeArgTypes)

    when(typeClassifier.isJavaListLike(eqTree(parentType))).thenReturn(false)

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes)
    )
  }

  test("infer scala.List(1, 2).length()") {
    val termApply = q"scala.List(1, 2).length()"

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


  // TODO - remove block of tests below once core term name sare fully qualified at start
  test("""infer List.apply[scala.Predef.String]("a", "b")""") {
    val termApply = q"""List.apply[scala.Predef.String]("a", "b")"""

    val argTypes = List(TypeSelects.ScalaString, TypeSelects.ScalaString)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaList, List(TypeSelects.ScalaString))

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermNames.List), eqTreeList(List(TypeSelects.ScalaString)), eqTo(2)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = List(Some(TypeSelects.ScalaString), Some(TypeSelects.ScalaString)),
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer Map.apply[scala.Predef.String, scala.Int](("a", 1), ("b", 2))""") {
    val termApply = q"""Map.apply[scala.Predef.String, scala.Int](("a", 1), ("b", 2))"""

    val appliedTypes = List(TypeSelects.ScalaString, TypeSelects.ScalaInt)
    val argTypes = List.fill(2)(Type.Tuple(appliedTypes))
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaMap, appliedTypes)

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermNames.Map), eqTreeList(appliedTypes), eqTo(2)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer Map.empty[scala.Predef.String, scala.Int]()""") {
    val termApply = q"""Map.empty[scala.Predef.String, scala.Int]()"""

    val appliedTypes = List(TypeSelects.ScalaString, TypeSelects.ScalaInt)
    val context = TermApplyInferenceContext(maybeArgTypes = Nil)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaMap, appliedTypes)

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermNames.Map), eqTreeList(appliedTypes), eqTo(0)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = Nil,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = Nil, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer Future.successful[scala.Int](1)""") {
    val termApply = q"""Future.successful[scala.Int](1)"""

    val appliedTypes = List(TypeSelects.ScalaInt)
    val argTypes = List(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaFuture, appliedTypes)

    when(initializerDeclDefInferrer.inferByAppliedTypes(eqTree(TermNames.Future), eqTreeList(appliedTypes), eqTo(1)))
      .thenReturn(PartialDeclDef(
        maybeParamTypes = maybeArgTypes,
        maybeReturnType = Some(expectedReturnType))
      )

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer Future.failed[scala.Int](new RuntimeException())") {
    val termApply = q"Future.failed[scala.Int](new RuntimeException())"

    val appliedTypes = List(TypeSelects.ScalaInt)
    val argTypes = List(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaFuture, appliedTypes)

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer List.apply("a", "b")""") {
    val termApply = q"""List.apply("a", "b")"""

    val argTypes = List(TypeSelects.ScalaString, TypeSelects.ScalaString)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaList, List(TypeSelects.ScalaString))

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

    val tupleTypes = List(TypeSelects.ScalaString, TypeSelects.ScalaInt)
    val argTypes = List.fill(2)(Type.Tuple(tupleTypes))
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaMap, tupleTypes)

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

    val expectedReturnType = Type.Apply(TypeSelects.ScalaMap, List(TypeNames.ScalaAny))

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

    val argTypes = List.fill(2)(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeNames.ScalaRange, List(TypeSelects.ScalaInt))

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

    val argTypes = List(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeArgTypes = maybeArgTypes)

    val expectedReturnType = Type.Apply(TypeSelects.ScalaFuture, List(TypeSelects.ScalaInt))

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

    val expectedReturnType = Type.Apply(TypeSelects.ScalaFuture, List(TypeSelects.ScalaAny))

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer List(1, 2).take(1)") {
    val termApply = q"List(1, 2).take(1)"

    val parentType = Type.Apply(TypeSelects.ScalaList, List(TypeSelects.ScalaInt))
    val argTypes = List(TypeSelects.ScalaInt)
    val maybeArgTypes = argTypes.map(Some(_))
    val context = TermApplyInferenceContext(maybeParentType = Some(parentType), maybeArgTypes = maybeArgTypes)

    val expectedReturnType = parentType

    when(typeClassifier.isJavaListLike(eqTree(parentType))).thenReturn(true)

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeParamTypes = maybeArgTypes, maybeReturnType = Some(expectedReturnType))
    )
  }

  test("infer List(1, 2).length()") {
    val termApply = q"List(1, 2).length()"

    val parentType = Type.Apply(TypeSelects.ScalaList, List(TypeSelects.ScalaInt))
    val context = TermApplyInferenceContext(maybeParentType = Some(parentType))
    val expectedReturnType = TypeSelects.ScalaInt

    when(typeClassifier.isJavaListLike(eqTree(parentType))).thenReturn(true)

    coreApplyDeclDefInferrer.infer(termApply, context) should equalPartialDeclDef(
      PartialDeclDef(maybeReturnType = Some(expectedReturnType))
    )
  }

  test("""infer print("abc"")""") {
    coreApplyDeclDefInferrer.infer(q"""print("abc")""", TermApplyInferenceContext()) should equalPartialDeclDef(
      PartialDeclDef(maybeReturnType = Some(TypeSelects.ScalaUnit))
    )
  }

  test("""infer println("abc"")""") {
    coreApplyDeclDefInferrer.infer(q"""println("abc")""", TermApplyInferenceContext()) should equalPartialDeclDef(
      PartialDeclDef(maybeReturnType = Some(TypeSelects.ScalaUnit))
    )
  }
  // TODO -end of block to remove

  test("infer x.toString()") {
    coreApplyDeclDefInferrer.infer(q"x.toString()", TermApplyInferenceContext()) should equalPartialDeclDef(
      PartialDeclDef(maybeReturnType = Some(TypeSelects.ScalaString))
    )
  }

  test("infer() when 'fun' is not inferrable") {
    coreApplyDeclDefInferrer.infer(q"blabla()", TermApplyInferenceContext()) should equalPartialDeclDef(PartialDeclDef())
  }
}
