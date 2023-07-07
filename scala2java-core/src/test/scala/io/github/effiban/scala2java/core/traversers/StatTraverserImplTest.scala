package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.matchers.StatContextMatcher.eqStatContext
import io.github.effiban.scala2java.core.renderers.{ImportRenderer, StatTermRenderer}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.Package
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.Ctor.Primary
import scala.meta.{Decl, Defn, Lit, Name, Pat, Pkg, Self, Template, Term, Type, XtensionQuasiquoteTerm}

class StatTraverserImplTest extends UnitTestSuite {

  private val statTermTraverser = mock[StatTermTraverser]
  private val statTermRenderer = mock[StatTermRenderer]
  private val importTraverser = mock[ImportTraverser]
  private val importRenderer = mock[ImportRenderer]
  private val pkgTraverser = mock[PkgTraverser]
  private val defnTraverser = mock[DefnTraverser]
  private val declTraverser = mock[DeclTraverser]

  private val statTraverser = new StatTraverserImpl(
    statTermTraverser,
    statTermRenderer,
    importTraverser,
    importRenderer,
    pkgTraverser,
    defnTraverser,
    declTraverser
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

  test("traverse Import when traverser returns an import") {
    val `import` = q"import somepackage1.SomeClass1"
    val traversedImport = q"import somepackage2.SomeClass2"

    doReturn(Some(traversedImport)).when(importTraverser).traverse(eqTree(`import`))

    statTraverser.traverse(`import`, StatContext(Package))

    verify(importRenderer).render(eqTree(traversedImport), eqStatContext(StatContext(Package)))
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

  test("traverse Defn.Val") {
    val defnVal = Defn.Val(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = Some(Type.Name("int")),
      rhs = Lit.Int(3)
    )

    doWrite("int myVal = 3").when(defnTraverser).traverse(eqTree(defnVal), ArgumentMatchers.eq(StatContext(JavaScope.Block)))

    statTraverser.traverse(defnVal, StatContext(JavaScope.Block))

    outputWriter.toString shouldBe "int myVal = 3"
  }

  test("traverse Decl.Val") {
    val declVal = Decl.Val(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = Type.Name("int")
    )

    doWrite("int myVal").when(declTraverser).traverse(eqTree(declVal), ArgumentMatchers.eq(StatContext(JavaScope.Block)))

    statTraverser.traverse(declVal, StatContext(JavaScope.Block))

    outputWriter.toString shouldBe "int myVal"
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
