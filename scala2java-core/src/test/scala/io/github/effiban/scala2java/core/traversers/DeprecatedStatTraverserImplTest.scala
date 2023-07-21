package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.renderers.contexts.{DefRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.renderers.{DeclRenderer, ImportRenderer, StatTermRenderer}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.traversers.results.{DeclDefTraversalResult, DeclVarTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.Package
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Ctor.Primary
import scala.meta.{Decl, Defn, Lit, Name, Pat, Pkg, Self, Template, Term, Type, XtensionQuasiquoteTerm}

@deprecated
class DeprecatedStatTraverserImplTest extends UnitTestSuite {

  private val statTermTraverser = mock[StatTermTraverser]
  private val statTermRenderer = mock[StatTermRenderer]
  private val importTraverser = mock[ImportTraverser]
  private val importRenderer = mock[ImportRenderer]
  private val pkgTraverser = mock[DeprecatedPkgTraverser]
  private val defnTraverser = mock[DeprecatedDefnTraverser]
  private val declTraverser = mock[DeclTraverser]
  private val declRenderer = mock[DeclRenderer]

  private val statTraverser = new DeprecatedStatTraverserImpl(
    statTermTraverser,
    statTermRenderer,
    importTraverser,
    importRenderer,
    pkgTraverser,
    defnTraverser,
    declTraverser,
    declRenderer
  )

  private val pkg = pkgDefinition()

  test("traverse Term.Name") {
    val termName = q"myName"
    val traversedTermName = q"myTraversedName"

    doReturn(traversedTermName).when(statTermTraverser).traverse(eqTree(termName))
    doWrite("myTraversedName").when(statTermRenderer).render(eqTree(traversedTermName))

    statTraverser.traverse(termName, StatContext(JavaScope.Class))

    outputWriter.toString shouldBe "myTraversedName"
  }

  test("traverse Import when traverser returns an import and Java scope is 'Package'") {
    val `import` = q"import somepackage1.SomeClass1"
    val traversedImport = q"import somepackage2.SomeClass2"

    doReturn(Some(traversedImport)).when(importTraverser).traverse(eqTree(`import`))

    statTraverser.traverse(`import`, StatContext(Package))

    verify(importRenderer).render(eqTree(traversedImport))
  }

  test("traverse Import when traverser returns an import and Java scope is 'Class'") {
    val `import` = q"import somepackage1.SomeClass1"
    val traversedImport = q"import somepackage2.SomeClass2"

    doReturn(Some(traversedImport)).when(importTraverser).traverse(eqTree(`import`))

    statTraverser.traverse(`import`, StatContext(JavaScope.Class))

    outputWriter.toString shouldBe "/* import somepackage2.SomeClass2 */"
  }

  test("traverse Import when traverser returns None") {
    val `import` = q"import somepackage1.SomeClass1"

    doReturn(None).when(importTraverser).traverse(eqTree(`import`))

    statTraverser.traverse(`import`, StatContext(Package))

    verifyNoMoreInteractions(importRenderer)
  }

  test("traverse Pkg") {
    doWrite(
      """/*
        |*  PACKAGE DEFINITION
        |*/""".stripMargin
    ).when(pkgTraverser).traverse(eqTree(pkg))

    statTraverser.traverse(pkg, StatContext(JavaScope.Unknown))

    outputWriter.toString shouldBe
      """/*
        |*  PACKAGE DEFINITION
        |*/""".stripMargin
  }

  test("traverse Defn.Var") {
    val defnVar = Defn.Var(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Some(Type.Name("int")),
      rhs = Some(Lit.Int(3))
    )

    doWrite("int myVar = 3").when(defnTraverser).traverse(eqTree(defnVar), eqTo(StatContext(JavaScope.Block)))

    statTraverser.traverse(defnVar, StatContext(JavaScope.Block))

    outputWriter.toString shouldBe "int myVar = 3"
  }

  test("traverse Decl.Var") {
    val declVar = q"private var myVar: Int"
    val traversedDeclVar = q"private var myTraversedVar: Int"
    val javaModifiers = List(JavaModifier.Private)
    val traversalResult = DeclVarTraversalResult(traversedDeclVar, javaModifiers)

    doReturn(traversalResult).when(declTraverser).traverse(eqTree(declVar), eqTo(StatContext(JavaScope.Block)))
    doWrite("private int myVar").when(declRenderer).render(eqTree(traversedDeclVar), eqTo(VarRenderContext(javaModifiers)))

    statTraverser.traverse(declVar, StatContext(JavaScope.Block))

    outputWriter.toString shouldBe "private int myVar"
  }

  test("traverse Decl.Def") {
    val declDef = q"private def foo(x: Int): Int"
    val traversedDeclDef = q"private def traversedFoo(xx: Int): Int"
    val javaModifiers = List(JavaModifier.Private)
    val traversalResult = DeclDefTraversalResult(traversedDeclDef, javaModifiers)

    doReturn(traversalResult).when(declTraverser).traverse(eqTree(declDef), eqTo(StatContext(JavaScope.Block)))
    doWrite("private int traversedFoo(int xx)").when(declRenderer).render(eqTree(traversedDeclDef), eqTo(DefRenderContext(javaModifiers)))

    statTraverser.traverse(declDef, StatContext(JavaScope.Block))

    outputWriter.toString shouldBe "private int traversedFoo(int xx)"
  }

  private def pkgDefinition() = {
    Pkg(ref = Term.Name("mypkg"),
      stats = List(
        traitDefinition(),
      )
    )
  }

  private def traitDefinition() = {
    Defn.Trait(
      mods = List(),
      name = Type.Name("MyTrait"),
      tparams = List(),
      ctor = Primary(
        mods = List(),
        name = Name.Anonymous(),
        paramss = List()
      ),
      templ = Template(
        early = List(),
        inits = List(),
        self = Self(Name.Anonymous(), None),
        stats = List(
          Decl.Val(
            mods = List(),
            pats = List(Pat.Var(Term.Name("myVal"))),
            decltpe = TypeNames.String
          )
        )
      )
    )
  }
}
