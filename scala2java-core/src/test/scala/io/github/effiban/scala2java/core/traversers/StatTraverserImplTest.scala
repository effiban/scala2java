package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.entities.JavaScope
import org.mockito.ArgumentMatchers

import scala.meta.Ctor.Primary
import scala.meta.{Decl, Defn, Import, Importee, Importer, Lit, Name, Pat, Pkg, Self, Template, Term, Type}

class StatTraverserImplTest extends UnitTestSuite {

  private val termTravserser = mock[TermTraverser]
  private val importTraverser = mock[ImportTraverser]
  private val pkgTraverser = mock[PkgTraverser]
  private val defnTraverser = mock[DefnTraverser]
  private val declTraverser = mock[DeclTraverser]

  private val statTraverser = new StatTraverserImpl(
    termTravserser,
    importTraverser,
    pkgTraverser,
    defnTraverser,
    declTraverser
  )

  private val pkg = pkgDefinition()

  test("traverse Term.Name") {
    val termName = Term.Name("myName")

    doWrite("myName").when(termTravserser).traverse(eqTree(termName))

    statTraverser.traverse(termName, StatContext(JavaScope.Class))

    outputWriter.toString shouldBe "myName"
  }

  test("traverse Import") {
    val `import` = Import(
      List(
        Importer(
          ref = Term.Name("somepackage"),
          importees = List(Importee.Name(Name.Indeterminate("SomeClass"))))
      )
    )

    doWrite(
      """import somepackage.SomeClass;
        |""".stripMargin)
      .when(importTraverser).traverse(eqTree(`import`), ArgumentMatchers.eq(StatContext(JavaScope.Package)))

    statTraverser.traverse(`import`, StatContext(JavaScope.Package))

    outputWriter.toString shouldBe
      """import somepackage.SomeClass;
        |""".stripMargin
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
