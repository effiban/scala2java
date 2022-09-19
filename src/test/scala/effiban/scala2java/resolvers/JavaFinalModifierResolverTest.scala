package effiban.scala2java.resolvers

import effiban.scala2java.classifiers.ObjectClassifier
import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{Templates, TypeNames}

import scala.meta.{Decl, Defn, Lit, Pat, Term, Tree}

class JavaFinalModifierResolverTest extends UnitTestSuite {

  private val TheDefnObject = Defn.Object(Nil, Term.Name("A"), Templates.Empty)
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

  private val objectClassifier = mock[ObjectClassifier]

  private val javaFinalModifierResolver = new JavaFinalModifierResolver(objectClassifier)

  private val RegularScenarios = Table(
    ("ScalaTree", "ScalaTreeDesc", "JavaScope", "ExpectedResult"),
    (TheDeclVal, DeclValDesc, JavaScope.Class, true),
    (TheDeclVal, DeclValDesc, JavaScope.UtilityClass, true),
    (TheDeclVal, DeclValDesc, JavaScope.Interface, false),
    (TheDeclVal, DeclValDesc, JavaScope.Block, true),
    (TheDefnVal, DefnValDesc, JavaScope.Class, true),
    (TheDefnVal, DefnValDesc, JavaScope.UtilityClass, true),
    (TheDefnVal, DefnValDesc, JavaScope.Interface, false),
    (TheDefnVal, DefnValDesc, JavaScope.Block, true),
    (TheDeclVar, DeclVarDesc, JavaScope.Class, false),
    (TheDeclVar, DeclVarDesc, JavaScope.UtilityClass, false),
    (TheDeclVar, DeclVarDesc, JavaScope.Interface, false),
    (TheDeclVar, DeclVarDesc, JavaScope.Block, false),
    (TheDefnVar, DefnVarDesc, JavaScope.Class, false),
    (TheDefnVar, DefnVarDesc, JavaScope.UtilityClass, false),
    (TheDefnVar, DefnVarDesc, JavaScope.Interface, false),
    (TheDefnVar, DefnVarDesc, JavaScope.Block, false),
    (TheTermParam, TermParamDesc, JavaScope.Class, true),
    (TheTermParam, TermParamDesc, JavaScope.MethodSignature, true),
    (TheTermParam, TermParamDesc, JavaScope.LambdaSignature, false),
  )

  forAll(RegularScenarios) {
    case (
      scalaTree: Tree,
      scalaTreeDesc: String,
      javaScope: JavaScope,
      expectedResult: Boolean) =>

      test(s"A '$scalaTreeDesc' in the scope '$javaScope' should ${if (expectedResult) "" else "not"} require 'final'") {
        resolve(scalaTree, javaScope) shouldBe (if (expectedResult) Some(JavaModifier.Final) else None)
      }
  }

  test("A Defn.Object should require 'final' when classified as 'standalone'") {
    when(objectClassifier.isStandalone(eqTree(TheDefnObject))).thenReturn(true)
    resolve(TheDefnObject, JavaScope.Package).value shouldBe JavaModifier.Final
  }

  test("A Defn.Object should not require 'final' when not classified as 'standalone'") {
    when(objectClassifier.isStandalone(eqTree(TheDefnObject))).thenReturn(false)
    resolve(TheDefnObject, JavaScope.Package) shouldBe None
  }

  private def resolve(scalaTree: Tree, javaScope: JavaScope) = {
    javaFinalModifierResolver.resolve(
      JavaModifiersContext(
        scalaTree = scalaTree,
        scalaMods = Nil,
        javaTreeType = JavaTreeType.Unknown,
        javaScope = javaScope
      ))
  }
}
