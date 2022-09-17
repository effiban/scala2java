package effiban.scala2java.resolvers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.{Decl, Defn, Lit, Pat, Term, Tree}

class JavaFinalModifierResolverTest extends UnitTestSuite {

  private val TheDeclVal = Decl.Val(Nil, List(Pat.Var(Term.Name("x"))), TypeNames.Int)
  private val TheDefnVal = Defn.Val(Nil, List(Pat.Var(Term.Name("x"))), None, Lit.Int(3))
  private val TheDeclVar = Decl.Var(Nil, List(Pat.Var(Term.Name("x"))), TypeNames.Int)
  private val TheDefnVar = Defn.Var(Nil, List(Pat.Var(Term.Name("x"))), Some(TypeNames.Int), Some(Lit.Int(3)))
  private val TheTermParam = Term.Param(Nil, Term.Name("myParam"), Some(TypeNames.Int), None)


  private val DeclValDesc = "Decl.Val"
  private val DefnValDesc = "Defn.Val"
  private val DeclVarDesc = "Decl.Var"
  private val DefnVarDesc = "Defn.Var"
  private val TermParamDesc = "Term.Param"

  private val RequiresFinalScenarios = Table(
    ("ScalaTree", "ScalaTreeDesc", "JavaScope", "ExpectedResult"),
    (TheDeclVal, DeclValDesc, JavaTreeType.Class, true),
    (TheDeclVal, DeclValDesc, JavaTreeType.Interface, false),
    (TheDeclVal, DeclValDesc, JavaTreeType.Block, true),
    (TheDefnVal, DefnValDesc, JavaTreeType.Class, true),
    (TheDefnVal, DefnValDesc, JavaTreeType.Interface, false),
    (TheDefnVal, DefnValDesc, JavaTreeType.Block, true),
    (TheDeclVar, DeclVarDesc, JavaTreeType.Class, false),
    (TheDeclVar, DeclVarDesc, JavaTreeType.Interface, false),
    (TheDeclVar, DeclVarDesc, JavaTreeType.Block, false),
    (TheDefnVar, DefnVarDesc, JavaTreeType.Class, false),
    (TheDefnVar, DefnVarDesc, JavaTreeType.Interface, false),
    (TheDefnVar, DefnVarDesc, JavaTreeType.Block, false),
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
        resolve(scalaTree, javaScope) shouldBe (if (expectedResult) Some(JavaModifier.Final) else None)
      }
  }

  private def resolve(scalaTree: Tree, javaScope: JavaTreeType) = {
    JavaFinalModifierResolver.resolve(
      JavaModifiersContext(
        scalaTree = scalaTree,
        scalaMods = Nil,
        javaTreeType = JavaTreeType.Unknown,
        javaScope = javaScope
      ))
  }
}
