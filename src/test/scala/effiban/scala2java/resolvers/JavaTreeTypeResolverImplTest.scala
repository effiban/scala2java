package effiban.scala2java.resolvers

import effiban.scala2java.contexts.JavaTreeTypeContext
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{PrimaryCtors, Templates, TypeNames}

import scala.meta.Term.Apply
import scala.meta.Type.Bounds
import scala.meta.{Decl, Defn, Import, Importee, Importer, Lit, Mod, Name, Pat, Pkg, Term, Tree, Type}

class JavaTreeTypeResolverImplTest extends UnitTestSuite {

  private val ThePkg = Pkg(Term.Name("a"), List(Import(List(Importer(Term.Name("b"), List(Importee.Name(Name.Indeterminate("c"))))))))
  private val TheCaseClassDef = Defn.Class(List(Mod.Case()), Type.Name("A"), Nil, PrimaryCtors.Empty, Templates.Empty)
  private val TheRegularClassDef = Defn.Class(Nil, Type.Name("A"), Nil, PrimaryCtors.Empty, Templates.Empty)
  private val TheObjectDef = Defn.Object(Nil, Term.Name("A"), Templates.Empty)
  private val TheTraitDef = Defn.Trait(Nil, Type.Name("A"), Nil, PrimaryCtors.Empty, Templates.Empty)
  private val TheDeclVal = Decl.Val(Nil, List(Pat.Var(Term.Name("x"))), TypeNames.Int)
  private val TheDefnVal = Defn.Val(Nil, List(Pat.Var(Term.Name("x"))), None, Lit.Int(3))
  private val TheDeclVar = Decl.Var(Nil, List(Pat.Var(Term.Name("x"))), TypeNames.Int)
  private val TheDefnVar = Defn.Var(Nil, List(Pat.Var(Term.Name("x"))), None, Some(Lit.Int(3)))
  private val TheDeclDef = Decl.Def(Nil, Term.Name("foo"), Nil, List(Nil), TypeNames.Int)
  private val TheDefnDef = Defn.Def(Nil, Term.Name("foo"), Nil, List(Nil), Some(TypeNames.Int), Term.Apply(Term.Name("bar"), Nil))
  private val TheDeclType = Decl.Type(Nil, Type.Name("MyType"), Nil, Bounds(None, None))
  private val TheDefnType = Defn.Type(Nil, Type.Name("MyType"), Nil, Type.Name("MyOtherType"))
  private val TheTermFunction = Term.Function(List(Term.Param(Nil, Term.Name("x"), None, None)), Apply(Term.Name("foo"), Nil))
  private val TheTermParam = Term.Param(Nil, Term.Name("x"), Some(TypeNames.Int), None)

  private val ResolverScenarios = Table(
    ("ScalaTreeDesc", "ScalaTree", "ScalaMods", "ExpectedJavaTreeType"),
    ("Pkg", ThePkg, Nil, JavaTreeType.Package),
    ("Defn.Class with Mod.Case", TheCaseClassDef, List(Mod.Case()), JavaTreeType.Record),
    ("Defn.Class with no mods", TheRegularClassDef, Nil, JavaTreeType.Class),
    ("Defn.Object", TheObjectDef, Nil, JavaTreeType.Class),
    ("Defn.Trait", TheTraitDef, Nil, JavaTreeType.Interface),
    ("Decl.Type", TheDeclType, Nil, JavaTreeType.Interface),
    ("Defn.Type", TheDefnType, Nil, JavaTreeType.Interface),
    ("Decl.Def", TheDeclDef, Nil, JavaTreeType.Method),
    ("Defn.Def", TheDefnDef, Nil, JavaTreeType.Method),
    ("Term.Function", TheTermFunction, Nil, JavaTreeType.Lambda),
    ("Decl.Val", TheDeclVal, Nil, JavaTreeType.Variable),
    ("Defn.Val", TheDefnVal, Nil, JavaTreeType.Variable),
    ("Decl.Var", TheDeclVar, Nil, JavaTreeType.Variable),
    ("Defn.Var", TheDefnVar, Nil, JavaTreeType.Variable),
    ("Term.Param", TheTermParam, Nil, JavaTreeType.Parameter),
    ("Lit.Int", Lit.Int(3), Nil, JavaTreeType.Unknown)
  )

  forAll(ResolverScenarios) { case (scalaTreeDesc: String, scalaTree: Tree, scalaMods: List[Mod], expectedJavaTreeType: JavaTreeType) =>
    test(s"A Scala '$scalaTreeDesc' should be resolved to Java type '$expectedJavaTreeType'") {
      JavaTreeTypeResolver.resolve(JavaTreeTypeContext(scalaTree, scalaMods)) shouldBe expectedJavaTreeType
    }
  }

}
