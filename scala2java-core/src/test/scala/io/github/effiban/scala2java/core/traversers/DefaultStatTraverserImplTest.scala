package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.matchers.StatTraversalResultScalatestMatcher.equalStatTraversalResult
import io.github.effiban.scala2java.core.traversers.results._
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.Package
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class DefaultStatTraverserImplTest extends UnitTestSuite {

  private val statTermTraverser = mock[StatTermTraverser]
  private val importTraverser = mock[ImportTraverser]
  private val defnTraverser = mock[DefnTraverser]
  private val declTraverser = mock[DeclTraverser]

  private val defaultStatTraverser = new DefaultStatTraverserImpl(
    statTermTraverser,
    importTraverser,
    defnTraverser,
    declTraverser
  )

  test("traverse Term.Name") {
    val termName = q"myName"
    val traversedTermName = q"myTraversedName"
    val traversalResult = SimpleStatTraversalResult(traversedTermName)

    doReturn(traversedTermName).when(statTermTraverser).traverse(eqTree(termName))

    defaultStatTraverser.traverse(termName, StatContext(JavaScope.Class)) should equalStatTraversalResult(traversalResult)
  }

  test("traverse Import when traverser returns an import") {
    val `import` = q"import somepackage1.SomeClass1"
    val traversedImport = q"import somepackage2.SomeClass2"
    val traversalResult = SimpleStatTraversalResult(traversedImport)

    doReturn(Some(traversedImport)).when(importTraverser).traverse(eqTree(`import`))

    defaultStatTraverser.traverse(`import`, StatContext(Package)) should equalStatTraversalResult(traversalResult)
  }

  test("traverse Import when traverser returns None") {
    val `import` = q"import somepackage1.SomeClass1"

    doReturn(None).when(importTraverser).traverse(eqTree(`import`))

    defaultStatTraverser.traverse(`import`, StatContext(Package)) should equalStatTraversalResult(EmptyStatTraversalResult)
  }

  test("traverse Decl.Var") {
    val declVar = q"private var myVar: Int"
    val traversedDeclVar = q"private var myTraversedVar: Int"
    val javaModifiers = List(JavaModifier.Private)
    val traversalResult = DeclVarTraversalResult(traversedDeclVar, javaModifiers)

    doReturn(traversalResult).when(declTraverser).traverse(eqTree(declVar), eqTo(StatContext(JavaScope.Block)))

    defaultStatTraverser.traverse(declVar, StatContext(JavaScope.Block)) should equalStatTraversalResult(traversalResult)
  }

  test("traverse Decl.Def") {
    val declDef = q"private def foo(x: Int): Int"
    val traversedDeclDef = q"private def traversedFoo(xx: Int): Int"
    val javaModifiers = List(JavaModifier.Private)
    val traversalResult = DeclDefTraversalResult(traversedDeclDef, javaModifiers)

    doReturn(traversalResult).when(declTraverser).traverse(eqTree(declDef), eqTo(StatContext(JavaScope.Block)))

    defaultStatTraverser.traverse(declDef, StatContext(JavaScope.Block)) should equalStatTraversalResult(traversalResult)
  }

  test("traverse Defn.Var") {
    val defnVar = q"private var myVar: Int = 3"
    val traversedDefnVar = q"private var myTraversedVar: Int = 3"
    val javaModifiers = List(JavaModifier.Private)
    val traversalResult = DefnVarTraversalResult(traversedDefnVar, javaModifiers)

    doReturn(traversalResult).when(defnTraverser).traverse(eqTree(defnVar), eqTo(StatContext(JavaScope.Block)))

    defaultStatTraverser.traverse(defnVar, StatContext(JavaScope.Block)) should equalStatTraversalResult(traversalResult)
  }

  test("traverse Defn.Def") {
    val defnDef = q"private def foo(x: Int): Int = doSomething(x)"
    val traversedDefnDef = q"private def traversedFoo(xx: Int): Int = doSomethingElse(x)"
    val javaModifiers = List(JavaModifier.Private)
    val traversalResult = DefnDefTraversalResult(traversedDefnDef, javaModifiers)

    doReturn(traversalResult).when(defnTraverser).traverse(eqTree(defnDef), eqTo(StatContext(JavaScope.Block)))

    defaultStatTraverser.traverse(defnDef, StatContext(JavaScope.Block)) should equalStatTraversalResult(traversalResult)
  }
}
