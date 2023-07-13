package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.entities.JavaModifier.Final
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{Templates, TypeNames}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Decl, Defn, Lit, Pat, Term, Tree}

class JavaFinalModifierResolverTest extends UnitTestSuite {

  private val TheDefnObject = Defn.Object(Nil, Term.Name("A"), Templates.Empty)
  private val TheDeclVal = Decl.Val(Nil, List(Pat.Var(Term.Name("x"))), TypeNames.Int)
  private val TheDefnVal = Defn.Val(Nil, List(Pat.Var(Term.Name("x"))), None, Lit.Int(3))
  private val TheDeclVar = Decl.Var(Nil, List(Pat.Var(Term.Name("x"))), TypeNames.Int)
  private val TheDefnVar = Defn.Var(Nil, List(Pat.Var(Term.Name("x"))), Some(TypeNames.Int), Some(Lit.Int(3)))
  private val TheTermParam = Term.Param(Nil, Term.Name("myParam"), Some(TypeNames.Int), None)


  private val DefnObjectDesc = "Defn.Object"
  private val DeclValDesc = "Decl.Val"
  private val DefnValDesc = "Defn.Val"
  private val DeclVarDesc = "Decl.Var"
  private val DefnVarDesc = "Defn.Var"
  private val TermParamDesc = "Term.Param"

  private val Scenarios = Table(
    ("ScalaTree", "ScalaTreeDesc", "JavaTreeType", "JavaScope", "ExpectedResult"),
    (TheDefnObject, DefnObjectDesc, JavaTreeType.Class, JavaScope.Package, true),
    (TheDefnObject, DefnObjectDesc, JavaTreeType.Enum, JavaScope.Package, false),
    (TheDeclVal, DeclValDesc, JavaTreeType.Variable, JavaScope.Class, true),
    (TheDeclVal, DeclValDesc, JavaTreeType.Variable, JavaScope.UtilityClass, true),
    (TheDeclVal, DeclValDesc, JavaTreeType.Variable, JavaScope.Interface, false),
    (TheDeclVal, DeclValDesc, JavaTreeType.Variable, JavaScope.Block, true),
    (TheDefnVal, DefnValDesc, JavaTreeType.Variable, JavaScope.Class, true),
    (TheDefnVal, DefnValDesc, JavaTreeType.Variable, JavaScope.UtilityClass, true),
    (TheDefnVal, DefnValDesc, JavaTreeType.Variable, JavaScope.Interface, false),
    (TheDefnVal, DefnValDesc, JavaTreeType.Variable, JavaScope.Block, true),
    (TheDeclVar, DeclVarDesc, JavaTreeType.Variable, JavaScope.Class, false),
    (TheDeclVar, DeclVarDesc, JavaTreeType.Variable, JavaScope.UtilityClass, false),
    (TheDeclVar, DeclVarDesc, JavaTreeType.Variable, JavaScope.Interface, false),
    (TheDeclVar, DeclVarDesc, JavaTreeType.Variable, JavaScope.Block, false),
    (TheDefnVar, DefnVarDesc, JavaTreeType.Variable, JavaScope.Class, false),
    (TheDefnVar, DefnVarDesc, JavaTreeType.Variable, JavaScope.UtilityClass, false),
    (TheDefnVar, DefnVarDesc, JavaTreeType.Variable, JavaScope.Interface, false),
    (TheDefnVar, DefnVarDesc, JavaTreeType.Variable, JavaScope.Block, false),
    (TheTermParam, TermParamDesc, JavaTreeType.Parameter, JavaScope.Class, false),
    (TheTermParam, TermParamDesc, JavaTreeType.Parameter, JavaScope.MethodSignature, true),
    (TheTermParam, TermParamDesc, JavaTreeType.Parameter, JavaScope.LambdaSignature, false),
  )

  forAll(Scenarios) {
    case (
      scalaTree: Tree,
      scalaTreeDesc: String,
      javaTreeType: JavaTreeType,
      javaScope: JavaScope,
      expectedResult: Boolean) =>

      test(s"A '$scalaTreeDesc' with java type '$javaTreeType' in scope '$javaScope' should ${if (expectedResult) "" else "not"} require 'final'") {
        resolve(scalaTree, javaTreeType, javaScope) shouldBe (if (expectedResult) Some(Final) else None)
      }
  }

  private def resolve(scalaTree: Tree, javaTreeType: JavaTreeType, javaScope: JavaScope) = {
    JavaFinalModifierResolver.resolve(
      ModifiersContext(
        scalaTree = scalaTree,
        javaTreeType = javaTreeType,
        javaScope = javaScope
      ))
  }
}
