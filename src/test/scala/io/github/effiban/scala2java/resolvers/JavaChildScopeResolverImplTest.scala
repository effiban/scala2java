package io.github.effiban.scala2java.resolvers

import io.github.effiban.scala2java.classifiers.ObjectClassifier
import io.github.effiban.scala2java.contexts.JavaChildScopeContext
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.entities.{JavaScope, JavaTreeType}
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.{PrimaryCtors, Templates, TypeNames}

import scala.meta.{Decl, Defn, Import, Importee, Importer, Name, Pat, Pkg, Term, Tree, Type}

class JavaChildScopeResolverImplTest extends UnitTestSuite {

  private val ThePkg = Pkg(Term.Name("a"), List(Import(List(Importer(Term.Name("b"), List(Importee.Name(Name.Indeterminate("c"))))))))
  private val TheClassDef = Defn.Class(Nil, Type.Name("A"), Nil, PrimaryCtors.Empty, Templates.Empty)
  private val TheObjectDef = Defn.Object(Nil, Term.Name("A"), Templates.Empty)
  private val TheTraitDef = Defn.Trait(Nil, Type.Name("A"), Nil, PrimaryCtors.Empty, Templates.Empty)
  private val TheDeclVal = Decl.Val(Nil, List(Pat.Var(Term.Name("x"))), TypeNames.Int)

  private val RegularResolverScenarios = Table(
    ("ScalaTreeDesc", "ScalaTree", "JavaTreeType", "ExpectedJavaChildScope"),
    ("Pkg", ThePkg, JavaTreeType.Package, JavaScope.Package),
    ("Defn.Class", TheClassDef, JavaTreeType.Class, JavaScope.Class),
    ("Defn.Class", TheClassDef, JavaTreeType.Record, JavaScope.Class),
    ("Defn.Trait", TheTraitDef, JavaTreeType.Interface, JavaScope.Interface),
    ("Decl.Val", TheDeclVal, JavaTreeType.Variable, JavaScope.Unknown)
  )

  private val objectClassifier = mock[ObjectClassifier]
  private val javaChildScopeResolver = new JavaChildScopeResolverImpl(objectClassifier)

  forAll(RegularResolverScenarios) { case (
    scalaTreeDesc: String,
    scalaTree: Tree,
    javaTreeType: JavaTreeType,
    expectedJavaChildScope: JavaScope) =>

    test(s"A Scala '$scalaTreeDesc' with Java type '$javaTreeType' should have a Java child scope of '$expectedJavaChildScope'") {
      javaChildScopeResolver.resolve(JavaChildScopeContext(scalaTree, javaTreeType)) shouldBe expectedJavaChildScope
    }
  }

  test("A Scala 'Defn.Object' should have a Java child scope of 'UtilityClass' when classified as 'standalone'") {
    when(objectClassifier.isStandalone(eqTree(TheObjectDef))).thenReturn(true)
    javaChildScopeResolver.resolve(JavaChildScopeContext(TheObjectDef, JavaTreeType.Class)) shouldBe JavaScope.UtilityClass
  }

  test("A Scala 'Defn.Object' should have a Java child scope of 'Class' when not classified as 'standalone'") {
    when(objectClassifier.isStandalone(eqTree(TheObjectDef))).thenReturn(false)
    javaChildScopeResolver.resolve(JavaChildScopeContext(TheObjectDef, JavaTreeType.Class)) shouldBe JavaScope.Class
  }
}
