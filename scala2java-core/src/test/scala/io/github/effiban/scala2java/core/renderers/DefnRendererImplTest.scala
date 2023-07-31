package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.ClassClassifier
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts._
import io.github.effiban.scala2java.core.renderers.matchers.CaseClassRenderContextMockitoMatcher.eqCaseClassRenderContext
import io.github.effiban.scala2java.core.renderers.matchers.ObjectRenderContextMockitoMatcher.eqObjectRenderContext
import io.github.effiban.scala2java.core.renderers.matchers.RegularClassRenderContextMockitoMatcher.eqRegularClassRenderContext
import io.github.effiban.scala2java.core.renderers.matchers.TraitRenderContextMockitoMatcher.eqTraitRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class DefnRendererImplTest extends UnitTestSuite {

  private val defnVarRenderer = mock[DefnVarRenderer]
  private val defnDefRenderer = mock[DefnDefRenderer]
  private val caseClassRenderer = mock[CaseClassRenderer]
  private val regularClassRenderer = mock[RegularClassRenderer]
  private val traitRenderer = mock[TraitRenderer]
  private val objectRenderer = mock[ObjectRenderer]
  private val classClassifier = mock[ClassClassifier]

  private val defnRenderer = new DefnRendererImpl(
    defnVarRenderer,
    defnDefRenderer,
    caseClassRenderer,
    regularClassRenderer,
    traitRenderer,
    objectRenderer,
    classClassifier
  )


  test("render() for Defn.Var with correct non-empty context") {
    val defnVar = q"private var myVar: Int = 3"
    val context = VarRenderContext(List(JavaModifier.Private))

    defnRenderer.render(defnVar, context)

    verify(defnVarRenderer).render(eqTree(defnVar), eqTo(context))
  }

  test("render() for Defn.Var with empty context should use default 'var' context") {
    val defnVar = q"private var myVar: Int = 3"

    defnRenderer.render(defnVar)

    verify(defnVarRenderer).render(eqTree(defnVar), eqTo(VarRenderContext()))
  }

  test("render() for Defn.Var with incorrect context should throw exception") {
    val defnVar = q"private var myVar: Int = 3"
    val context = DefRenderContext(List(JavaModifier.Private))

    intercept[IllegalStateException] {
      defnRenderer.render(defnVar, context)
    }
  }

  test("render() for Defn.Def with correct non-empty context") {
    val defnDef = q"def myMethod(x: Int) = doSomething(x)"
    val context = DefRenderContext(List(JavaModifier.Public))

    defnRenderer.render(defnDef, context)

    verify(defnDefRenderer).render(eqTree(defnDef), eqTo(context))
  }

  test("render() for Defn.Def with empty context should use default 'def' context") {
    val defnDef = q"def myMethod(x: Int) = doSomething(x)"

    defnRenderer.render(defnDef)

    verify(defnDefRenderer).render(eqTree(defnDef), eqTo(DefRenderContext()))
  }

  test("render() for Defn.Def with incorrect context should throw exception") {
    val defnDef = q"def myMethod(x: Int) = doSomething(x)"
    val context = VarRenderContext(List(JavaModifier.Public))

    intercept[IllegalStateException] {
      defnRenderer.render(defnDef, context)
    }
  }

  test("render() for a case class") {
    val defnClass =
      q"""
      case class MyClass(val1: Int, val2: String)
      """

    val context = CaseClassRenderContext(javaModifiers = List(JavaModifier.Public))

    when(classClassifier.isCase(eqTree(defnClass))).thenReturn(true)

    defnRenderer.render(defnClass, context)

    verify(caseClassRenderer).render(eqTree(defnClass), eqCaseClassRenderContext(context))
  }

  test("render() for a case class with incorrect context should throw exception") {
    val defnClass =
      q"""
      case class MyClass(val1: Int, val2: String)
      """

    val context = RegularClassRenderContext(javaModifiers = List(JavaModifier.Public))

    when(classClassifier.isRegular(eqTree(defnClass))).thenReturn(false)

    intercept[IllegalStateException] {
      defnRenderer.render(defnClass, context)
    }
  }

  test("render() for a regular class") {
    val defnClass =
      q"""
      class MyClass(val1: Int, val2: String) {
          def foo(): Unit = {
              doSomething(val1, val2)
          }
      }
      """

    val context = RegularClassRenderContext(javaModifiers =  List(JavaModifier.Public))

    when(classClassifier.isRegular(eqTree(defnClass))).thenReturn(true)

    defnRenderer.render(defnClass, context)

    verify(regularClassRenderer).render(eqTree(defnClass), eqRegularClassRenderContext(context))
  }

  test("render() for a regular class with incorrect context should throw exception") {
    val defnClass =
      q"""
      class MyClass(val1: Int, val2: String) {
          def foo(): Unit = {
              doSomething(val1, val2)
          }
      }
      """

    val context = CaseClassRenderContext(javaModifiers = List(JavaModifier.Public))

    when(classClassifier.isCase(eqTree(defnClass))).thenReturn(false)

    intercept[IllegalStateException] {
      defnRenderer.render(defnClass, context)
    }
  }

  test("render() for Trait") {
    val defnTrait =
      q"""
      trait MyTrait {
          def foo(): Int
      }
      """

    val context = TraitRenderContext(javaModifiers = List(JavaModifier.Public))

    defnRenderer.render(defnTrait, context)

    verify(traitRenderer).render(eqTree(defnTrait), eqTraitRenderContext(context))
  }

  test("render() for Trait with incorrect context should throw exception") {
    val defnTrait =
      q"""
      trait MyTrait {
          def foo(): Int
      }
      """

    val context = RegularClassRenderContext(javaModifiers = List(JavaModifier.Public))

    intercept[IllegalStateException] {
      defnRenderer.render(defnTrait, context)
    }
  }

  test("render() for Object") {
    val defnObject =
      q"""
      object MyObject {
          def inc(x: Int): Int = x + 1
      }
      """

    val context = ObjectRenderContext(javaModifiers = List(JavaModifier.Public))

    defnRenderer.render(defnObject, context)

    verify(objectRenderer).render(eqTree(defnObject), eqObjectRenderContext(context))
  }

  test("render() for Object with incorrect context should throw exception") {
    val defnObject =
      q"""
      object MyObject {
          def inc(x: Int): Int = x + 1
      }
      """

    val context = TraitRenderContext(javaModifiers = List(JavaModifier.Public))

    intercept[IllegalStateException] {
      defnRenderer.render(defnObject, context)
    }
  }
}
