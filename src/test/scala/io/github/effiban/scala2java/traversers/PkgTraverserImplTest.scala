package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.PrimaryCtors

import scala.meta.{Decl, Defn, Import, Importee, Importer, Name, Pat, Pkg, Self, Template, Term, Type}

class PkgTraverserImplTest extends UnitTestSuite {

  private val TheImport = Import(
    List(
      Importer(
        ref = Term.Name("extpkg"),
        importees = List(Importee.Name(Name.Indeterminate("ExtClass")))
      )
    )
  )

  private val TheClass = Defn.Class(
    mods = List(),
    name = Type.Name("MyClass"),
    tparams = List(),
    ctor = PrimaryCtors.Empty,
    templ = Template(
      early = List(),
      inits = List(),
      self = Self(Name.Anonymous(), None),
      stats = List(
        Decl.Val(mods = List(), pats = List(Pat.Var(Term.Name("myVal1"))), decltpe = Type.Name("Long"))
      )
    )
  )

  private val termRefTraverser = mock[TermRefTraverser]
  private val pkgStatListTraverser = mock[PkgStatListTraverser]

  private val pkgTraverser = new PkgTraverserImpl(termRefTraverser, pkgStatListTraverser)


  test("traverse()") {
    val pkgRef = Term.Select(Term.Name("mypkg"), Term.Name("myinnerpkg"))

    val stats = List(TheImport, TheClass)

    doWrite("mypkg.myinnerpkg").when(termRefTraverser).traverse(eqTree(pkgRef))
    doWrite(
      """/*
        |*  IMPORT DEFINITION
        |*/
        |/*
        |*  CLASS DEFINITION
        |*/
        |""".stripMargin)
      .when(pkgStatListTraverser).traverse(eqTreeList(stats))

    pkgTraverser.traverse(Pkg(ref = pkgRef, stats = stats))

    outputWriter.toString shouldBe
      """package mypkg.myinnerpkg;
        |
        |/*
        |*  IMPORT DEFINITION
        |*/
        |/*
        |*  CLASS DEFINITION
        |*/
        |""".stripMargin
  }
}
