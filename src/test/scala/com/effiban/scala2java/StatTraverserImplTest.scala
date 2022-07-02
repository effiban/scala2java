package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import com.effiban.scala2java.testtrees.TypeNames

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

    statTraverser.traverse(termName)

    outputWriter.toString shouldBe "myName"
  }

  test("traverse Import") {
    val `import` = Import(List(Importer(ref = Term.Name("somepackage"), importees = List(Importee.Name(Name.Indeterminate("SomeClass"))))))

    doWrite(
      """import somepackage.SomeClass;
        |""".stripMargin)
      .when(importTraverser).traverse(eqTree(`import`))

    statTraverser.traverse(`import`)

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

    statTraverser.traverse(pkg)

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

    doWrite("int myVal = 3").when(defnTraverser).traverse(eqTree(defnVal))

    statTraverser.traverse(defnVal)

    outputWriter.toString shouldBe "int myVal = 3"
  }

  test("traverse Decl.Val") {
    val declVal = Decl.Val(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = Type.Name("int")
    )

    doWrite("int myVal").when(declTraverser).traverse(eqTree(declVal))

    statTraverser.traverse(declVal)

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
