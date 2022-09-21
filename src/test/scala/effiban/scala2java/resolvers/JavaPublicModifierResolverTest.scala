package effiban.scala2java.resolvers

import effiban.scala2java.classifiers.ModsClassifier
import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.{JavaModifier, JavaScope, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{PrimaryCtors, Templates, TypeNames}

import scala.meta.{Decl, Defn, Lit, Mod, Name, Pat, Term, Tree, Type}

class JavaPublicModifierResolverTest extends UnitTestSuite {

  private val ScalaModsPublic = List(Mod.Case())
  private val ScalaModsNonPublic = List(Mod.Private(Name.Anonymous()), Mod.Protected(Name.Anonymous()))

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

  private val scalaModsClassifier = mock[ModsClassifier]

  private val ScenariosWhenScalaModsPublic = Table(
    ("Desc", "ScalaTree", "JavaTreeType", "JavaScope", "ExpectedMaybeModifier"),
    ("outer class", TheClass, JavaTreeType.Class, JavaScope.Package, Some(JavaModifier.Public)),
    ("outer class in sealed hierarchy", TheClass, JavaTreeType.Class, JavaScope.Sealed, Some(JavaModifier.Public)),
    ("inner class of class", TheClass, JavaTreeType.Class, JavaScope.Class, Some(JavaModifier.Public)),
    ("inner class of utility class", TheClass, JavaTreeType.Class, JavaScope.UtilityClass, Some(JavaModifier.Public)),
    ("inner class of interface", TheClass, JavaTreeType.Class, JavaScope.Interface, None),
    ("outer enum", TheClass, JavaTreeType.Enum, JavaScope.Package, Some(JavaModifier.Public)),
    ("outer interface", TheTrait, JavaTreeType.Interface, JavaScope.Package, Some(JavaModifier.Public)),
    ("outer interface in sealed hierarchy", TheTrait, JavaTreeType.Interface, JavaScope.Sealed, Some(JavaModifier.Public)),
    ("inner interface of class", TheTrait, JavaTreeType.Interface, JavaScope.Class, Some(JavaModifier.Public)),
    ("inner interface of utility class", TheTrait, JavaTreeType.Interface, JavaScope.UtilityClass, Some(JavaModifier.Public)),
    ("inner interface of interface", TheTrait, JavaTreeType.Interface, JavaScope.Interface, None),
    ("class method", TheDefnDef, JavaTreeType.Method, JavaScope.Class, Some(JavaModifier.Public)),
    ("enum method", TheDefnDef, JavaTreeType.Method, JavaScope.Enum, Some(JavaModifier.Public)),
    ("interface method definition", TheDefnDef, JavaTreeType.Method, JavaScope.Interface, Some(JavaModifier.Default)),
    ("interface method declaration", TheDeclDef, JavaTreeType.Method, JavaScope.Interface, None),
    ("class variable", TheVal, JavaTreeType.Variable, JavaScope.Class, Some(JavaModifier.Public)),
    ("utility class variable", TheVal, JavaTreeType.Variable, JavaScope.UtilityClass, Some(JavaModifier.Public)),
    ("enum variable", TheVal, JavaTreeType.Variable, JavaScope.Enum, Some(JavaModifier.Public)),
    ("interface variable", TheVal, JavaTreeType.Variable, JavaScope.Interface, None),
    ("method variable", TheVal, JavaTreeType.Variable, JavaScope.MethodSignature, None),
    ("lambda variable", TheVal, JavaTreeType.Variable, JavaScope.LambdaSignature, None),
    ("class (ctor.) parameter", TheVal, JavaTreeType.Parameter, JavaScope.Class, None),
    ("method parameter", TheVal, JavaTreeType.Parameter, JavaScope.MethodSignature, None),
    ("lambda parameter", TheVal, JavaTreeType.Parameter, JavaScope.LambdaSignature, None)
  )

  forAll(ScenariosWhenScalaModsPublic) { case (
    desc: String,
    scalaTree: Tree,
    javaTreeType: JavaTreeType,
    javaScope: JavaScope,
    expectedMaybeModifier: Option[JavaModifier]) =>
    test(s"Public modifier generated for $desc should be: '$expectedMaybeModifier'") {
      when(scalaModsClassifier.arePublic(eqTreeList(ScalaModsPublic))).thenReturn(true)
      resolve(scalaTree, ScalaModsPublic, javaTreeType, javaScope) shouldBe expectedMaybeModifier
    }
  }

  test("No public modifier generated when scala mods are not public") {
    when(scalaModsClassifier.arePublic(eqTreeList(ScalaModsNonPublic))).thenReturn(false)
    resolve(TheVal, ScalaModsPublic, JavaTreeType.Parameter, JavaScope.MethodSignature) shouldBe None
  }

  private def resolve(scalaTree: Tree,
                      scalaMods: List[Mod],
                      javaTreeType: JavaTreeType,
                      javaScope: JavaScope): Option[JavaModifier] = {
    JavaPublicModifierResolver.resolve(
      JavaModifiersContext(
        scalaTree = scalaTree,
        scalaMods = scalaMods,
        javaTreeType = javaTreeType,
        javaScope = javaScope
      ))
  }
}
