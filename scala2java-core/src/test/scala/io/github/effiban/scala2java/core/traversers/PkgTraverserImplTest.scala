package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.generators.JavaImportersProvider
import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, TermNames}

import scala.meta.{Decl, Defn, Import, Importee, Importer, Name, Pat, Pkg, Self, Template, Term, Type}

class PkgTraverserImplTest extends UnitTestSuite {

  private val ArbitraryImport = Import(
    List(
      Importer(
        ref = Term.Name("extpkg"),
        importees = List(Importee.Name(Name.Indeterminate("ExtClass")))
      )
    )
  )

  private val JavaImporters = List(
    Importer(ref = Term.Select(TermNames.Java, Term.Name("lang")), importees = List(Importee.Wildcard())),
    Importer(ref = Term.Select(TermNames.Java, Term.Name("util")), importees = List(Importee.Wildcard()))
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
  private val javaImportersProvider = mock[JavaImportersProvider]

  private val pkgTraverser = new PkgTraverserImpl(
    termRefTraverser,
    pkgStatListTraverser,
    javaImportersProvider
  )


  test("traverse()") {
    val pkgRef = Term.Select(Term.Name("mypkg"), Term.Name("myinnerpkg"))

    val stats = List(ArbitraryImport, TheClass)
    val expectedEnrichedStats = Import(JavaImporters) +: stats

    doWrite("mypkg.myinnerpkg").when(termRefTraverser).traverse(eqTree(pkgRef))
    when(javaImportersProvider.provide()).thenReturn(JavaImporters)
    doWrite(
      """/*
        |*  IMPORT DEFINITIONS
        |*/
        |/*
        |*  CLASS DEFINITION
        |*/
        |""".stripMargin)
      .when(pkgStatListTraverser).traverse(eqTreeList(expectedEnrichedStats))

    pkgTraverser.traverse(Pkg(ref = pkgRef, stats = stats))

    outputWriter.toString shouldBe
      """package mypkg.myinnerpkg;
        |
        |/*
        |*  IMPORT DEFINITIONS
        |*/
        |/*
        |*  CLASS DEFINITION
        |*/
        |""".stripMargin
  }
}
