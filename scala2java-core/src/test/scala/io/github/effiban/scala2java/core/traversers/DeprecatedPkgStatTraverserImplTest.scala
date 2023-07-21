package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ClassOrTraitContext, StatContext}
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.matchers.ClassOrTraitContextMatcher.eqClassOrTraitContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.PrimaryCtors
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers

import scala.meta.{Decl, Defn, Import, Importee, Importer, Name, Pat, Self, Template, Term, Type}

@deprecated
class DeprecatedPkgStatTraverserImplTest extends UnitTestSuite {

  private val TheImport = Import(
    List(
      Importer(
        ref = Term.Name("extpkg"),
        importees = List(Importee.Name(Name.Indeterminate("ExtClass")))
      )
    )
  )

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


  private val classTraverser = mock[DeprecatedClassTraverser]
  private val traitTraverser = mock[DeprecatedTraitTraverser]
  private val objectTraverser = mock[DeprecatedObjectTraverser]
  private val statTraverser = mock[DeprecatedStatTraverser]

  private val pkgStatTraverser = new DeprecatedPkgStatTraverserImpl(
    classTraverser,
    traitTraverser,
    objectTraverser,
    statTraverser
  )

  test("traverse() for trait which is not sealed or child of sealed") {
    pkgStatTraverser.traverse(TheTrait, SealedHierarchies())

    verify(traitTraverser).traverse(
      eqTree(TheTrait),
      ArgumentMatchers.eq(ClassOrTraitContext(javaScope = JavaScope.Package))
    )
  }

  test("traverse() for sealed trait which is not child of sealed") {
    val childNames = List(Type.Name("Child1"), Type.Name("Child2"))
    pkgStatTraverser.traverse(TheTrait, SealedHierarchies(Map(TheTrait.name -> childNames)))

    verify(traitTraverser).traverse(
      eqTree(TheTrait),
      eqClassOrTraitContext(ClassOrTraitContext(javaScope = JavaScope.Package, permittedSubTypeNames = childNames))
    )
  }

  test("traverse() for non-sealed trait which is a child of sealed") {
    val childNames = List(TheTrait.name, Type.Name("Other"))
    pkgStatTraverser.traverse(TheTrait, SealedHierarchies(Map(Type.Name("Parent") -> childNames)))

    verify(traitTraverser).traverse(
      eqTree(TheTrait),
      eqClassOrTraitContext(ClassOrTraitContext(javaScope = JavaScope.Sealed))
    )
  }

  test("traverse() for sealed trait which is also a child of sealed") {
    val traitChildNames = List(Type.Name("Child1"), Type.Name("Child2"))
    pkgStatTraverser.traverse(TheTrait, SealedHierarchies(
      Map(
        Type.Name("Parent") -> List(TheTrait.name, Type.Name("Other")),
        TheTrait.name -> traitChildNames))
    )

    verify(traitTraverser).traverse(
      eqTree(TheTrait),
      eqClassOrTraitContext(ClassOrTraitContext(javaScope = JavaScope.Sealed, permittedSubTypeNames = traitChildNames))
    )
  }

  test("traverse() for class which is not sealed or child of sealed") {
    pkgStatTraverser.traverse(TheClass, SealedHierarchies())

    verify(classTraverser).traverse(
      eqTree(TheClass),
      ArgumentMatchers.eq(ClassOrTraitContext(javaScope = JavaScope.Package))
    )
  }

  test("traverse() for sealed class which is not child of sealed") {
    val childNames = List(Type.Name("Child1"), Type.Name("Child2"))
    pkgStatTraverser.traverse(TheClass, SealedHierarchies(Map(TheClass.name -> childNames)))

    verify(classTraverser).traverse(
      eqTree(TheClass),
      eqClassOrTraitContext(ClassOrTraitContext(javaScope = JavaScope.Package, permittedSubTypeNames = childNames))
    )
  }

  test("traverse() for non-sealed class which is a child of sealed") {
    val childNames = List(TheClass.name, Type.Name("Other"))
    pkgStatTraverser.traverse(TheClass, SealedHierarchies(Map(Type.Name("Parent") -> childNames)))

    verify(classTraverser).traverse(
      eqTree(TheClass),
      eqClassOrTraitContext(ClassOrTraitContext(javaScope = JavaScope.Sealed))
    )
  }

  test("traverse() for sealed class which is also a child of sealed") {
    val traitChildNames = List(Type.Name("Child1"), Type.Name("Child2"))
    pkgStatTraverser.traverse(TheClass, SealedHierarchies(
      Map(
        Type.Name("Parent") -> List(TheClass.name, Type.Name("Other")),
        TheClass.name -> traitChildNames))
    )

    verify(classTraverser).traverse(
      eqTree(TheClass),
      eqClassOrTraitContext(ClassOrTraitContext(javaScope = JavaScope.Sealed, permittedSubTypeNames = traitChildNames))
    )
  }

  test("traverse() for object which is not a child of sealed") {
    pkgStatTraverser.traverse(TheObject, SealedHierarchies())

    verify(objectTraverser).traverse(
      eqTree(TheObject),
      ArgumentMatchers.eq(StatContext(javaScope = JavaScope.Package))
    )
  }

  test("traverse() for object which is a child of sealed") {
    val childNames = List(TheObject.name, Type.Name("Other"))

    pkgStatTraverser.traverse(TheObject, SealedHierarchies(Map(Type.Name("Parent") -> childNames)))

    verify(objectTraverser).traverse(
      eqTree(TheObject),
      ArgumentMatchers.eq(StatContext(javaScope = JavaScope.Sealed))
    )
  }

  test("traverse() for import") {
    pkgStatTraverser.traverse(TheImport, SealedHierarchies())

    verify(statTraverser).traverse(eqTree(TheImport), ArgumentMatchers.eq(StatContext(javaScope = JavaScope.Package)))
  }
}
