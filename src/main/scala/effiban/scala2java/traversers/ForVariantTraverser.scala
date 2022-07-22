package effiban.scala2java.traversers

import effiban.scala2java.entities.TraversalConstants.JavaPlaceholder
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.{Apply, Param, Select}
import scala.meta.{Enumerator, Lit, Pat, Term}

private[traversers] trait ForVariantTraverser {
  val intermediateFunctionName: Term.Name
  val finalFunctionName: Term.Name

  def termTraverser: TermTraverser

  implicit val javaWriter: JavaWriter

  import javaWriter._

  def traverse(enumerators: List[Enumerator], body: Term): Unit = {
    termTraverser.traverse(translateFor(enumerators, body))
  }

  private def translateFor(enumerators: List[Enumerator], body: Term): Term = {
    enumerators match {
      case Nil =>
        writeComment("ERROR - for comprehension without enumerators")
        Lit.Unit()
      case theEnumerators =>
        val currentEnumerator :: nextEnumerators = theEnumerators

        val (param, adjustedTerm) = currentEnumerator match {
          case Enumerator.Generator(pat, term) => (pat2Param(pat), term)
          //TODO should be converted to partial function
          case Enumerator.CaseGenerator(pat, term) => (pat2Param(pat), term)
          //TODO not sure what this is
          case Enumerator.Val(pat, term) => (pat2Param(pat), term)
          //TODO handle guard, for now returning dummy values
          case Enumerator.Guard(_) => (Param(Nil, Term.Name(""), None, None), Lit.Unit())
        }

        nextEnumerators match {
          case Nil =>
            // This enumerator is the last - next is the final function invocation ('forEach' or 'map')
            Apply(Select(adjustedTerm, finalFunctionName), List(Term.Function(List(param), body)))
          case theNextEnumerators =>
            // This enumerator is not the last - invoke the intermediate function and recursively translate the rest
            Apply(
              Select(adjustedTerm, intermediateFunctionName),
              List(Term.Function(List(param), translateFor(theNextEnumerators, body)))
            )
        }
    }
  }

  private def pat2Param(pat: Pat) = {
    //TODO - improve
    val name = pat match {
      case Pat.Wildcard() => JavaPlaceholder
      case _ => pat.toString()
    }
    Param(mods = List.empty, name = Term.Name(name), decltpe = None, default = None)
  }
}
