package effiban.scala2java.resolvers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{PrimaryCtors, Templates, TypeNames}

import scala.meta.{Decl, Defn, Lit, Pat, Term, Tree, Type}

class JavaPublicModifierResolverTest extends UnitTestSuite {

  private val TheClass = Defn.Class(
    mods = Nil,
    name = Type.Name("MyClass"),
    tparams = Nil,
    ctor = PrimaryCtors.Empty,
    templ = Templates.Empty
  )

  private val TheTrait = Defn.Trait(
    mods = Nil,
    name = Type.Name("MyTrait"),
    tparams = Nil,
    ctor = PrimaryCtors.Empty,
    templ = Templates.Empty
  )

  private val TheDeclDef = Decl.Def(
    mods = Nil,
    name = Term.Name("myMethod"),
    tparams = Nil,
    paramss = List(Nil),
    decltpe = Type.Name("Unit")
  )

  private val TheDefnDef = Defn.Def(
    mods = Nil,
    name = Term.Name("myMethod"),
    tparams = Nil,
    paramss = List(Nil),
    decltpe = Some(TypeNames.Unit),
    body = Lit.Int(3)
  )

  private val TheVal = Defn.Val(
    mods = Nil,
    pats = List(Pat.Var(Term.Name("x"))),
    decltpe = Some(TypeNames.Int),
    rhs = Lit.Int(3)
  )

  private val ResolverScenarios = Table(
    ("Desc", "ScalaTree", "JavaTreeType", "JavaScope", "ExpectedMaybeModifier"),
    ("outer class", TheClass, JavaTreeType.Class, JavaTreeType.Package, Some(JavaModifier.Public)),
    ("inner class of class", TheClass, JavaTreeType.Class, JavaTreeType.Class, Some(JavaModifier.Public)),
    ("inner class of interface", TheClass, JavaTreeType.Class, JavaTreeType.Interface, None),
    ("outer enum", TheClass, JavaTreeType.Enum, JavaTreeType.Package, Some(JavaModifier.Public)),
    ("outer interface", TheTrait, JavaTreeType.Interface, JavaTreeType.Package, Some(JavaModifier.Public)),
    ("inner interface of class", TheTrait, JavaTreeType.Interface, JavaTreeType.Class, Some(JavaModifier.Public)),
    ("inner interface of interface", TheTrait, JavaTreeType.Interface, JavaTreeType.Interface, None),
    ("class method", TheDefnDef, JavaTreeType.Method, JavaTreeType.Class, Some(JavaModifier.Public)),
    ("enum method", TheDefnDef, JavaTreeType.Method, JavaTreeType.Enum, Some(JavaModifier.Public)),
    ("interface method definition", TheDefnDef, JavaTreeType.Method, JavaTreeType.Interface, Some(JavaModifier.Default)),
    ("interface method declaration", TheDeclDef, JavaTreeType.Method, JavaTreeType.Interface, None),
    ("class variable", TheVal, JavaTreeType.Variable, JavaTreeType.Class, Some(JavaModifier.Public)),
    ("enum variable", TheVal, JavaTreeType.Variable, JavaTreeType.Enum, Some(JavaModifier.Public)),
    ("interface variable", TheVal, JavaTreeType.Variable, JavaTreeType.Interface, None),
    ("method variable", TheVal, JavaTreeType.Variable, JavaTreeType.Method, None),
    ("lambda variable", TheVal, JavaTreeType.Variable, JavaTreeType.Lambda, None),
    ("class (ctor.) parameter", TheVal, JavaTreeType.Parameter, JavaTreeType.Class, None),
    ("method parameter", TheVal, JavaTreeType.Parameter, JavaTreeType.Method, None),
    ("lambda parameter", TheVal, JavaTreeType.Parameter, JavaTreeType.Lambda, None)
  )

  forAll(ResolverScenarios) { case (
    desc: String,
    scalaTree: Tree,
    javaTreeType: JavaTreeType,
    javaScope: JavaTreeType,
    expectedMaybeModifier: Option[JavaModifier]) =>
    test(s"Public modifier generated for $desc should be: '$expectedMaybeModifier'") {
      resolve(scalaTree, javaTreeType, javaScope) shouldBe expectedMaybeModifier
    }
  }

  private def resolve(scalaTree: Tree, javaTreeType: JavaTreeType, javaScope: JavaTreeType): Option[JavaModifier] = {
    JavaPublicModifierResolver.resolve(
      JavaModifiersContext(
        scalaTree = scalaTree,
        scalaMods = Nil,
        javaTreeType = javaTreeType,
        javaScope = javaScope
      ))
  }
}
