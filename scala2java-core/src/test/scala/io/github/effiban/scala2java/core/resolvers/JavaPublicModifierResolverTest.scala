package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.classifiers.ModsClassifier
import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.entities.JavaModifier.Public
import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.core.entities.{JavaModifier, JavaTreeType}
import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, Templates, TypeNames}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Decl, Defn, Lit, Mod, Name, Pat, Term, Tree, Type}

class JavaPublicModifierResolverTest extends UnitTestSuite {

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

  private val TheValWithoutMods = Defn.Val(
    mods = Nil,
    pats = List(Pat.Var(Term.Name("x"))),
    decltpe = Some(TypeNames.Int),
    rhs = Lit.Int(3)
  )

  private val TheValWithNonPublicMods = Defn.Val(
    mods = Nil,
    pats = List(Pat.Var(Term.Name("x"))),
    decltpe = Some(TypeNames.Int),
    rhs = Lit.Int(3)
  )

  private val scalaModsClassifier = mock[ModsClassifier]

  private val ScenariosWhenScalaModsPublic = Table(
    ("Desc", "ScalaTree", "JavaTreeType", "JavaScope", "ExpectedMaybeModifier"),
    ("outer class", TheClass, JavaTreeType.Class, JavaScope.Package, Some(Public)),
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
    ("class variable", TheValWithoutMods, JavaTreeType.Variable, JavaScope.Class, Some(JavaModifier.Public)),
    ("utility class variable", TheValWithoutMods, JavaTreeType.Variable, JavaScope.UtilityClass, Some(JavaModifier.Public)),
    ("enum variable", TheValWithoutMods, JavaTreeType.Variable, JavaScope.Enum, Some(JavaModifier.Public)),
    ("interface variable", TheValWithoutMods, JavaTreeType.Variable, JavaScope.Interface, None),
    ("method variable", TheValWithoutMods, JavaTreeType.Variable, JavaScope.MethodSignature, None),
    ("lambda variable", TheValWithoutMods, JavaTreeType.Variable, JavaScope.LambdaSignature, None),
    ("class (ctor.) parameter", TheValWithoutMods, JavaTreeType.Parameter, JavaScope.Class, None),
    ("method parameter", TheValWithoutMods, JavaTreeType.Parameter, JavaScope.MethodSignature, None),
    ("lambda parameter", TheValWithoutMods, JavaTreeType.Parameter, JavaScope.LambdaSignature, None)
  )

  forAll(ScenariosWhenScalaModsPublic) { case (
    desc: String,
    scalaTree: Tree,
    javaTreeType: JavaTreeType,
    javaScope: JavaScope,
    expectedMaybeModifier: Option[JavaModifier]) =>
    test(s"Public modifier generated for $desc should be: '$expectedMaybeModifier'") {
      when(scalaModsClassifier.arePublic(Nil)).thenReturn(true)
      resolve(scalaTree, javaTreeType, javaScope) shouldBe expectedMaybeModifier
    }
  }

  test("No public modifier generated when scala mods are not public") {
    when(scalaModsClassifier.arePublic(eqTreeList(ScalaModsNonPublic))).thenReturn(false)
    resolve(TheValWithNonPublicMods, JavaTreeType.Parameter, JavaScope.MethodSignature) shouldBe None
  }

  private def resolve(scalaTree: Tree,
                      javaTreeType: JavaTreeType,
                      javaScope: JavaScope): Option[JavaModifier] = {
    JavaPublicModifierResolver.resolve(
      ModifiersContext(
        scalaTree = scalaTree,
        javaTreeType = javaTreeType,
        javaScope = javaScope
      ))
  }
}
