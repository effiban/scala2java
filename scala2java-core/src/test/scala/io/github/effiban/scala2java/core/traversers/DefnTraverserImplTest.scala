package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.{JavaKeyword, JavaModifier}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results._
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class DefnTraverserImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaScope.Class)
  private val TheClassOrTraitContext = ClassOrTraitContext(JavaScope.Class)

  private val defnVarTraverser = mock[DefnVarTraverser]
  private val defnDefTraverser = mock[DefnDefTraverser]
  private val traitTraverser = mock[TraitTraverser]
  private val classTraverser = mock[ClassTraverser]
  private val objectTraverser = mock[ObjectTraverser]

  private val defnTraverser = new DefnTraverserImpl(
    defnVarTraverser,
    defnDefTraverser,
    traitTraverser,
    classTraverser,
    objectTraverser
  )


  test("traverse() for Defn.Var when Defn.Var returned") {

    val defnVar = q"private var myVar: Int = 3"
    val traversedDefnVar = q"private var myTraversedVar: Int = 33"
    val javaModifiers = List(JavaModifier.Private)
    val traversalResult = DefnVarTraversalResult(traversedDefnVar, javaModifiers)

    doReturn(traversalResult).when(defnVarTraverser).traverse(eqTree(defnVar), eqTo(TheStatContext))

    defnTraverser.traverse(defnVar, TheStatContext).structure shouldBe traversedDefnVar.structure
  }

  test("traverse() for Defn.Var when Decl.Var returned") {

    val defnVar = q"private var myVar: Int = 3"
    val declVar = q"private var myVar: Int"
    val javaModifiers = List(JavaModifier.Private)
    val traversalResult = DeclVarTraversalResult(declVar, javaModifiers)

    doReturn(traversalResult).when(defnVarTraverser).traverse(eqTree(defnVar), eqTo(TheStatContext))

    defnTraverser.traverse(defnVar, TheStatContext).structure shouldBe declVar.structure
  }

  test("traverse() for Defn.Def") {
    val defnDef = q"def myMethod(x: Int) = doSomething(x)"
    val traversedDefnDef = q"def myMethod2(xx: Int) = doSomething2(xx)"
    val traversalResult = DefnDefTraversalResult(traversedDefnDef, List(JavaModifier.Public))

    doReturn(traversalResult).when(defnDefTraverser).traverse(eqTree(defnDef), eqTo(DefnDefContext(TheStatContext.javaScope)))

    defnTraverser.traverse(defnDef, TheStatContext).structure shouldBe traversedDefnDef.structure
  }

  test("traverse() for Trait") {
    val defnTrait = q"trait MyTrait { var x: Int }"
    val traversedTrait = q"trait MyTraversedTrait { var xx: Int }"

    doReturn(traversedTrait).when(traitTraverser).traverse(eqTree(defnTrait))

    defnTraverser.traverse(defnTrait, TheStatContext).structure shouldBe traversedTrait.structure
  }

  test("traverse() for Defn.Class") {
    val defnClass = q"class MyClass { def foo(x: Int) = x + 1 }"
    val traversedClass = q"class MyTraversedClass { def traversedFoo(xx: Int) = xx + 1 }"

    doReturn(traversedClass).when(classTraverser).traverse(eqTree(defnClass), eqTo(TheClassOrTraitContext))

    defnTraverser.traverse(defnClass, TheStatContext).structure shouldBe traversedClass.structure
  }

  test("traverse() for Defn.Object") {
    val defnObject =
      q"""
      object MyObject {
        final var x: Int = 3
      }
      """
    val traversalResult = ObjectTraversalResult(
      javaModifiers = List(JavaModifier.Public, JavaModifier.Final),
      javaTypeKeyword = JavaKeyword.Class,
      name = q"MyObject",
      statResults = List(DefnVarTraversalResult(q"final var xx: Int = 33"))
    )

    doReturn(traversalResult).when(objectTraverser).traverse(eqTree(defnObject), eqTo(TheStatContext))

    defnTraverser.traverse(defnObject, TheStatContext).structure shouldBe traversalResult.tree.structure
  }
}
