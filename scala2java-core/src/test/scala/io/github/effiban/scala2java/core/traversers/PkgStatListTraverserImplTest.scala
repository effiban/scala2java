package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.matchers.SealedHierarchiesMatcher.eqSealedHierarchies
import io.github.effiban.scala2java.core.resolvers.SealedHierarchiesResolver
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.PrimaryCtors
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Decl, Defn, Name, Pat, Self, Template, Term, Type}

class PkgStatListTraverserImplTest extends UnitTestSuite {

  private val TheTrait = Defn.Trait(
    mods = List(),
    name = Type.Name("MyTrait"),
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

  private val TheObject = Defn.Object(
    mods = List(),
    name = Term.Name("MyObject"),
    templ = Template(
      early = List(),
      inits = List(),
      self = Self(Name.Anonymous(), None),
      stats = List(
        Decl.Val(mods = List(), pats = List(Pat.Var(Term.Name("myVal2"))), decltpe = Type.Name("Long"))
      )
    )
  )


  private val pkgStatTraverser = mock[PkgStatTraverser]
  private val sealedHierarchiesResolver = mock[SealedHierarchiesResolver]

  private val pkgStatListTraverser = new PkgStatListTraverserImpl(
    pkgStatTraverser,
    sealedHierarchiesResolver
  )

  test("traverse()") {
    val stats = List(TheTrait, TheObject)
    val expectedSealedHierarchies = SealedHierarchies(Map(Type.Name("A") -> List(Name.Indeterminate("B"))))

    when(sealedHierarchiesResolver.traverse(eqTreeList(stats))).thenReturn(expectedSealedHierarchies)

    doWrite(
      """/*
        |*  TRAIT DEFINITION
        |*/
        |""".stripMargin)
      .when(pkgStatTraverser).traverse(stat = eqTree(TheTrait), sealedHierarchies = eqSealedHierarchies(expectedSealedHierarchies))

    doWrite(
      """/*
        |*  OBJECT DEFINITION
        |*/
        |""".stripMargin)
      .when(pkgStatTraverser).traverse(stat = eqTree(TheObject), sealedHierarchies = eqSealedHierarchies(expectedSealedHierarchies))

    pkgStatListTraverser.traverse(stats)

    outputWriter.toString shouldBe
      """/*
        |*  TRAIT DEFINITION
        |*/
        |/*
        |*  OBJECT DEFINITION
        |*/
        |""".stripMargin
  }

}
