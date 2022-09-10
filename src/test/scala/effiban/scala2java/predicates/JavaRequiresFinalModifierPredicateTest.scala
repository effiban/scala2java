package effiban.scala2java.predicates

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.{Decl, Defn, Lit, Pat, Term, Tree}

class JavaRequiresFinalModifierPredicateTest extends UnitTestSuite {

  private val TheDeclVal = Decl.Val(Nil, List(Pat.Var(Term.Name("x"))), TypeNames.Int)
  private val TheDefnVal = Defn.Val(Nil, List(Pat.Var(Term.Name("x"))), None, Lit.Int(3))
  private val TheDeclVar = Decl.Var(Nil, List(Pat.Var(Term.Name("x"))), TypeNames.Int)
  private val TheTermParam = Term.Param(Nil, Term.Name("myParam"), Some(TypeNames.Int), None)


  private val DeclValDesc = "Decl.Val"
  private val DefnValDesc = "Defn.Val"
  private val DeclVarDesc = "Decl.Var"
  private val TermParamDesc = "Term.Param"

  private val RequiresFinalScenarios = Table(
    ("ScalaTree", "ScalaTreeDesc", "JavaScope", "ExpectedResult"),
    (TheDeclVal, DeclValDesc, JavaTreeType.Class, true),
    (TheDeclVal, DeclValDesc, JavaTreeType.Interface, false),
    (TheDeclVal, DeclValDesc, JavaTreeType.Method, true),
    (TheDeclVal, DeclValDesc, JavaTreeType.Lambda, true),
    (TheDefnVal, DefnValDesc, JavaTreeType.Class, true),
    (TheDefnVal, DefnValDesc, JavaTreeType.Interface, false),
    (TheDefnVal, DefnValDesc, JavaTreeType.Method, true),
    (TheDefnVal, DefnValDesc, JavaTreeType.Lambda, true),
    (TheDeclVar, DeclVarDesc, JavaTreeType.Class, false),
    (TheDeclVar, DeclVarDesc, JavaTreeType.Interface, false),
    (TheDeclVar, DeclVarDesc, JavaTreeType.Method, false),
    (TheDeclVar, DeclVarDesc, JavaTreeType.Lambda, false),
    (TheTermParam, TermParamDesc, JavaTreeType.Class, true),
    (TheTermParam, TermParamDesc, JavaTreeType.Method, true),
    (TheTermParam, TermParamDesc, JavaTreeType.Lambda, false),
  )

  forAll(RequiresFinalScenarios) {
    case (
      scalaTree: Tree,
      scalaTreeDesc: String,
      javaScope: JavaTreeType,
      expectedResult: Boolean) =>

      test(s"A '$scalaTreeDesc' in the scope '$javaScope' should ${if (expectedResult) "" else "not"} require 'final'") {
        requiresFinal(scalaTree, javaScope) shouldBe expectedResult
      }
  }

  private def requiresFinal(scalaTree: Tree, javaScope: JavaTreeType) = {
    JavaRequiresFinalModifierPredicate.apply(
      JavaModifiersContext(
        scalaTree = scalaTree,
        scalaMods = Nil,
        javaTreeType = JavaTreeType.Unknown,
        javaScope = javaScope
      ))
  }
}
