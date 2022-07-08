package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Ctor.Primary
import scala.meta.{Decl, Defn, Import, Importee, Importer, Name, Pat, Pkg, Self, Template, Term, Type}

class PkgTraverserImplTest extends UnitTestSuite {

  private val termRefTraverser = mock[TermRefTraverser]
  private val statTraverser = mock[StatTraverser]

  private val pkgTraverser = new PkgTraverserImpl(termRefTraverser, statTraverser)

  private val traitDefn = traitDefinition()
  private val objectDefn = objectDefinition()


  test("traverse()") {
    val pkgRef = Term.Select(Term.Name("mypkg"), Term.Name("myinnerpkg"))
    val `import` = Import(
      List(
        Importer(
          ref = Term.Name("extpkg"),
          importees = List(Importee.Name(Name.Indeterminate("ExtClass")))
        )
      )
    )

    doWrite("mypkg.myinnerpkg").when(termRefTraverser).traverse(eqTree(pkgRef))
    doWrite(
      """import extpkg.ExtClass;
        |""".stripMargin).when(statTraverser).traverse(eqTree(`import`))
    doWrite(
      """/*
        |*  TRAIT DEFINITION
        |*/
        |""".stripMargin)
      .when(statTraverser).traverse(eqTree(traitDefn))
    doWrite(
      """/*
        |*  OBJECT DEFINITION
        |*/
        |""".stripMargin)
      .when(statTraverser).traverse(eqTree(objectDefn))

    pkgTraverser.traverse(
      Pkg(ref = pkgRef,
        stats = List(
          `import`,
          traitDefn,
          objectDefn,
        )
      ),
    )

    outputWriter.toString shouldBe
      """package mypkg.myinnerpkg;
        |
        |import extpkg.ExtClass;
        |/*
        |*  TRAIT DEFINITION
        |*/
        |/*
        |*  OBJECT DEFINITION
        |*/
        |""".stripMargin
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
            pats = List(Pat.Var(Term.Name("myVal1"))),
            decltpe = Type.Name("Long")
          )
        )
      )
    )
  }

  private def objectDefinition() = {
    Defn.Object(
      mods = List(),
      name = Term.Name("MyObject"),
      templ = Template(
        early = List(),
        inits = List(),
        self = Self(Name.Anonymous(), None),
        stats = List(
          Decl.Val(
            mods = List(),
            pats = List(Pat.Var(Term.Name("myVal2"))),
            decltpe = Type.Name("Long")
          )
        )
      )
    )
  }

}