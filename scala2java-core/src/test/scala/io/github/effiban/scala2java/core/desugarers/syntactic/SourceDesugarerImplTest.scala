package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Enumerator.Generator
import scala.meta.Term.{Apply, For, ForYield, Select}
import scala.meta.{Lit, Term, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteSource, XtensionQuasiquoteTerm}

class SourceDesugarerImplTest extends UnitTestSuite {

  private val termInterpolateDesugarer = mock[TermInterpolateDesugarer]
  private val forDesugarer = mock[ForDesugarer]
  private val forYieldDesugarer = mock[ForYieldDesugarer]
  private val declValToDeclVarDesugarer = mock[DeclValToDeclVarDesugarer]
  private val defnValToDefnVarDesugarer = mock[DefnValToDefnVarDesugarer]
  private val defnTypeToTraitDesugarer = mock[DefnTypeToTraitDesugarer]
  private val termApplyInfixDesugarer = mock[TermApplyInfixDesugarer]

  private val sourceDesugarer = new SourceDesugarerImpl(
    termInterpolateDesugarer,
    forDesugarer,
    forYieldDesugarer,
    declValToDeclVarDesugarer,
    defnValToDefnVarDesugarer,
    defnTypeToTraitDesugarer,
    termApplyInfixDesugarer
  )

  test("desugar when has a Term.Interpolate should return a desugared equivalent") {
    val interpolationArgs = List(Term.Name("x"), Term.Name("y"))

    // s"start $x middle $y end"
    val termInterpolate = Term.Interpolate(
      prefix = Term.Name("s"),
      parts = List(Lit.String("start "), Lit.String(" middle "), Lit.String(" end")),
      args = interpolationArgs
    )

    // String.format("start %s middle %s end", x, y)
    val expectedJavaStringFormat = Apply(
      fun = Select(Term.Name("String"), Term.Name("format")),
      args = Lit.String("start %s middle %s end") +: termInterpolate.args
    )

    val source =
      source"""
      package dummy

      class MyClass {
         def foo(): String = {
           $termInterpolate
         }
      }
      """

    doReturn(expectedJavaStringFormat).when(termInterpolateDesugarer).desugar(eqTree(termInterpolate))

    val desugaredSource = sourceDesugarer.desugar(source)

    val maybeDesugaredTermApply = desugaredSource.collect { case termApply: Term.Apply => termApply }.headOption

    maybeDesugaredTermApply.value.structure shouldBe expectedJavaStringFormat.structure
  }

  test("desugar when has a Term.For should return a desugared equivalent") {
    val enumerators = List(
      Generator(pat = p"x", rhs = q"xs"),
      Generator(pat = p"y", rhs = q"ys")
    )
    val body = q"handle(x, y)"
    val `for` = For(enums = enumerators, body = body)

    val source =
      source"""
      package dummy

      class MyClass {
         def foo(): Unit = {
           for {
             x <- xs
             y <- ys
           } handle(x, y)
         }
      }
      """

    val expectedTermApply = q"xs.foreach(x => ys.foreach(y => handle(x, y)))"

    doReturn(expectedTermApply).when(forDesugarer).desugar(eqTree(`for`))

    val desugaredSource = sourceDesugarer.desugar(source)

    val maybeTermApply = desugaredSource.collect { case termApply@Term.Apply(q"xs.foreach", _) => termApply }.headOption

    maybeTermApply.value.structure shouldBe expectedTermApply.structure
  }

  test("desugar when has a Term.ForYield should return a desugared equivalent") {
    val enumerators = List(
      Generator(pat = p"x", rhs = q"xs"),
      Generator(pat = p"y", rhs = q"ys")
    )
    val body = q"result(x, y)"
    val forYield = ForYield(enums = enumerators, body = body)

    val source =
      source"""
      package dummy

      class MyClass {
         def foo(): Unit = {
           for {
             x <- xs
             y <- ys
           } yield result(x, y)
         }
      }
      """

    val expectedTermApply = q"xs.flatMap(x => ys.map(y => result(x, y)))"

    doReturn(expectedTermApply).when(forYieldDesugarer).desugar(eqTree(forYield))

    val desugaredSource = sourceDesugarer.desugar(source)

    val maybeTermApply = desugaredSource.collect { case termApply@Term.Apply(q"xs.flatMap", _) => termApply }.headOption

    maybeTermApply.value.structure shouldBe expectedTermApply.structure
  }

  test("desugar when has a Decl.Val should return a corresponding Decl.Var") {

    val declVal = q"val x: Int"
    val source =
      source"""
      package dummy

      class MyClass {
        $declVal
      }
      """

    val expectedDeclVar = q"final var x: Int"
    val expectedDesugaredSource =
      source"""
      package dummy

      class MyClass {
        $expectedDeclVar
      }
      """

    doReturn(expectedDeclVar).when(declValToDeclVarDesugarer).desugar(eqTree(declVal))

    sourceDesugarer.desugar(source).structure shouldBe expectedDesugaredSource.structure
  }

  test("desugar when has a Defn.Val should return a corresponding Defn.Var") {

    val defnVal = q"val x: Int = 3"
    val source =
      source"""
      package dummy

      class MyClass {
        $defnVal
      }
      """

    val expectedDefnVar = q"final var x: Int = 3"
    val expectedDesugaredSource =
      source"""
      package dummy

      class MyClass {
        $expectedDefnVar
      }
      """

    doReturn(expectedDefnVar).when(defnValToDefnVarDesugarer).desugar(eqTree(defnVal))

    sourceDesugarer.desugar(source).structure shouldBe expectedDesugaredSource.structure
  }

  test("desugar when has a Defn.Type should return a corresponding Trait") {

    val defnType = q"type MyType = List[Int]"
    val source =
      source"""
      package dummy

      class MyClass {
        $defnType
      }
      """

    val expectedTrait = q"trait MyType extends List[Int]()"
    val expectedDesugaredSource =
      source"""
      package dummy

      class MyClass {
        $expectedTrait
      }
      """

    doReturn(expectedTrait).when(defnTypeToTraitDesugarer).desugar(eqTree(defnType))

    sourceDesugarer.desugar(source).structure shouldBe expectedDesugaredSource.structure
  }


  test("desugar when has a Term.ApplyInfix should return a corresponding Term") {
    val source =
      source"""
      package dummy

      class MyClass {
        var x = Map(a -> b)
      }
      """

    val expectedDesugaredSource =
      source"""
      package dummy

      class MyClass {
        var x = Map((a, b))
      }
      """

    doReturn(q"(a, b)").when(termApplyInfixDesugarer).desugar(eqTree(q"a -> b"))

    sourceDesugarer.desugar(source).structure shouldBe expectedDesugaredSource.structure
  }

  test("desugar with no inner desugared elems should return unchanged") {
    val source =
      source"""
      package dummy

      class MyClass {
      }
      """

    sourceDesugarer.desugar(source).structure shouldBe source.structure
  }
}
