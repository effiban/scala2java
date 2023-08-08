package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.Package
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class DefaultStatTraverserImplTest extends UnitTestSuite {

  private val statTermTraverser = mock[StatTermTraverser]
  private val importTraverser = mock[ImportTraverser]
  private val pkgTraverser = mock[PkgTraverser]
  private val defnTraverser = mock[DefnTraverser]
  private val declTraverser = mock[DeclTraverser]

  private val defaultStatTraverser = new DefaultStatTraverserImpl(
    statTermTraverser,
    importTraverser,
    pkgTraverser,
    defnTraverser,
    declTraverser
  )

  test("traverse Term.Name") {
    val termName = q"myName"
    val traversedTermName = q"myTraversedName"

    doReturn(traversedTermName).when(statTermTraverser).traverse(eqTree(termName))

    defaultStatTraverser.traverse(termName, StatContext(JavaScope.Class)).value.structure shouldBe traversedTermName.structure
  }

  test("traverse Import when traverser returns an import") {
    val `import` = q"import somepackage1.SomeClass1"
    val traversedImport = q"import somepackage2.SomeClass2"

    doReturn(Some(traversedImport)).when(importTraverser).traverse(eqTree(`import`))

    defaultStatTraverser.traverse(`import`, StatContext(Package)).value.structure shouldBe traversedImport.structure
  }

  test("traverse Import when traverser returns None") {
    val `import` = q"import somepackage1.SomeClass1"

    doReturn(None).when(importTraverser).traverse(eqTree(`import`))

    defaultStatTraverser.traverse(`import`, StatContext(Package)) shouldBe None
  }

  test("traverse Pkg") {
    val pkg = q"package a.b"
    val traversedPkg = q"package aa.bb"

    doReturn(traversedPkg).when(pkgTraverser).traverse(eqTree(pkg))

    defaultStatTraverser.traverse(pkg).value.structure shouldBe traversedPkg.structure
  }

  test("traverse Decl.Var") {
    val declVar = q"private var myVar: Int"
    val traversedDeclVar = q"private var myTraversedVar: Int"

    doReturn(traversedDeclVar).when(declTraverser).traverse(eqTree(declVar))

    defaultStatTraverser.traverse(declVar, StatContext(JavaScope.Block)).value.structure shouldBe traversedDeclVar.structure
  }

  test("traverse Decl.Def") {
    val declDef = q"private def foo(x: Int): Int"
    val traversedDeclDef = q"private def traversedFoo(xx: Int): Int"

    doReturn(traversedDeclDef).when(declTraverser).traverse(eqTree(declDef))

    defaultStatTraverser.traverse(declDef, StatContext(JavaScope.Block)).value.structure shouldBe traversedDeclDef.structure
  }

  test("traverse Defn.Var") {
    val defnVar = q"private var myVar: Int = 3"
    val traversedDefnVar = q"private var myTraversedVar: Int = 3"

    doReturn(traversedDefnVar).when(defnTraverser).traverse(eqTree(defnVar), eqTo(StatContext(JavaScope.Block)))

    defaultStatTraverser.traverse(defnVar, StatContext(JavaScope.Block)).value.structure shouldBe traversedDefnVar.structure
  }

  test("traverse Defn.Def") {
    val defnDef = q"private def foo(x: Int): Int = doSomething(x)"
    val traversedDefnDef = q"private def traversedFoo(xx: Int): Int = doSomethingElse(x)"

    doReturn(traversedDefnDef).when(defnTraverser).traverse(eqTree(defnDef), eqTo(StatContext(JavaScope.Block)))

    defaultStatTraverser.traverse(defnDef, StatContext(JavaScope.Block)).value.structure shouldBe traversedDefnDef.structure
  }
}
