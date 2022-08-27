package effiban.scala2java.resolvers

import effiban.scala2java.entities.{JavaModifier, JavaTreeType}
import effiban.scala2java.resolvers.JavaModifierImplyingPublicResolver.resolve
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{PrimaryCtors, Templates, TypeNames}

import scala.meta.{Decl, Defn, Lit, Pat, Term, Type}

class JavaModifierImplyingPublicResolverTest extends UnitTestSuite {

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

  test("resolve() for outer class should return 'public'") {
    resolve(TheClass, JavaTreeType.Class, JavaTreeType.Package).value shouldBe JavaModifier.Public
  }

  test("resolve() for inner class of class should return 'public'") {
    resolve(TheClass, JavaTreeType.Class, JavaTreeType.Class).value shouldBe JavaModifier.Public
  }

  test("resolve() for inner class of interface should return None") {
    resolve(TheClass, JavaTreeType.Class, JavaTreeType.Interface) shouldBe None
  }

  test("resolve() for outer interface should return 'public'") {
    resolve(TheTrait, JavaTreeType.Interface, JavaTreeType.Package).value shouldBe JavaModifier.Public
  }

  test("resolve() for inner interface of class should return 'public'") {
    resolve(TheTrait, JavaTreeType.Interface, JavaTreeType.Class).value shouldBe JavaModifier.Public
  }

  test("resolve() for inner interface of interface should return None") {
    resolve(TheTrait, JavaTreeType.Interface, JavaTreeType.Interface) shouldBe None
  }

  test("resolve() for class method should return 'public'") {
    resolve(TheDefnDef, JavaTreeType.Method, JavaTreeType.Class).value shouldBe JavaModifier.Public
  }

  test("resolve() for interface method definition should return 'default'") {
    resolve(TheDefnDef, JavaTreeType.Method, JavaTreeType.Interface).value shouldBe JavaModifier.Default
  }

  test("resolve() for interface method declaration should return None") {
    resolve(TheDeclDef, JavaTreeType.Method, JavaTreeType.Interface) shouldBe None
  }

  test("resolve() for class variable should return 'public'") {
    resolve(TheVal, JavaTreeType.Variable, JavaTreeType.Class).value shouldBe JavaModifier.Public
  }

  test("resolve() for interface variable should return None") {
    resolve(TheVal, JavaTreeType.Variable, JavaTreeType.Interface) shouldBe None
  }

  test("resolve() for method variable should return None") {
    resolve(TheVal, JavaTreeType.Variable, JavaTreeType.Method) shouldBe None
  }

  test("resolve() for lambda variable should return None") {
    resolve(TheVal, JavaTreeType.Variable, JavaTreeType.Lambda) shouldBe None
  }

  test("resolve() for class (ctor.) parameter should return None since the 'public' will be transferred to a generated member") {
    resolve(TheVal, JavaTreeType.Parameter, JavaTreeType.Class) shouldBe None
  }

  test("resolve() for method parameter should return None") {
    resolve(TheVal, JavaTreeType.Parameter, JavaTreeType.Method) shouldBe None
  }

  test("resolve() for lambda parameter should return None") {
    resolve(TheVal, JavaTreeType.Parameter, JavaTreeType.Lambda) shouldBe None
  }
}
