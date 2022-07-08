package effiban.scala2java.traversers

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{PrimaryCtors, Templates}

import scala.meta.{Defn, Mod, Type}

class ClassTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val caseClassTraverser = mock[CaseClassTraverser]
  private val regularClassTraverser = mock[RegularClassTraverser]

  private val classTraverser = new ClassTraverserImpl(caseClassTraverser, regularClassTraverser)

  test("traverse when case class") {
    val classDef = Defn.Class(
      mods = List(Mod.Case()),
      name = ClassName,
      tparams = Nil,
      ctor = PrimaryCtors.Empty,
      templ = Templates.Empty
    )

    classTraverser.traverse(classDef)

    verify(caseClassTraverser).traverse(classDef)
  }

  test("traverse when regular class") {
    val classDef = Defn.Class(
      mods = Nil,
      name = ClassName,
      tparams = Nil,
      ctor = PrimaryCtors.Empty,
      templ = Templates.Empty
    )

    classTraverser.traverse(classDef)

    verify(regularClassTraverser).traverse(classDef)
  }
}