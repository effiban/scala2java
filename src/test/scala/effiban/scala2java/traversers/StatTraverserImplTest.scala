package effiban.scala2java.traversers

import effiban.scala2java.contexts.StatContext
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.{Method, Package, Unknown}
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
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

    statTraverser.traverse(termName, StatContext(JavaTreeType.Class))

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
      .when(importTraverser).traverse(eqTree(`import`), ArgumentMatchers.eq(StatContext(Package)))

    statTraverser.traverse(`import`, StatContext(Package))

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

    statTraverser.traverse(pkg, StatContext(Unknown))

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

    doWrite("int myVal = 3").when(defnTraverser).traverse(eqTree(defnVal), ArgumentMatchers.eq(StatContext(Method)))

    statTraverser.traverse(defnVal, StatContext(Method))

    outputWriter.toString shouldBe "int myVal = 3"
  }

  test("traverse Decl.Val") {
    val declVal = Decl.Val(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = Type.Name("int")
    )

    doWrite("int myVal").when(declTraverser).traverse(eqTree(declVal), ArgumentMatchers.eq(StatContext(Method)))

    statTraverser.traverse(declVal, StatContext(Method))

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
