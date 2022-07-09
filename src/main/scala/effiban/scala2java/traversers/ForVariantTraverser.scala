package effiban.scala2java.traversers

import effiban.scala2java.entities.TraversalConstants.JavaPlaceholder
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.{Apply, Param, Select}
import scala.meta.{Enumerator, Lit, Pat, Term}

trait ForVariantTraverser {
  def traverse(enumerators: List[Enumerator], body: Term, finalFunctionName: Term.Name): Unit
}

private[traversers] class ForVariantTraverserImpl(termTraverser: => TermTraverser)
                                                 (implicit javaWriter: JavaWriter) extends ForVariantTraverser {

  import javaWriter._

  override def traverse(enumerators: List[Enumerator],
                        body: Term,
                        finalFunctionName: Term.Name): Unit = {
    termTraverser.traverse(translateFor(enumerators, body, finalFunctionName))
  }

  private def translateFor(enumerators: List[Enumerator],
                           body: Term,
                           finalFunctionName: Term.Name): Term = {
    enumerators match {
      case Nil =>
        writeComment("ERROR - for comprehension without enumerators")
        Lit.Unit()
      case theEnumerators =>
        val currentEnumerator :: nextEnumerators = theEnumerators

        val (param, adjustedTerm) = currentEnumerator match {
          case Enumerator.Generator(pat, term) => (pat2Param(pat), term)
          //TODO should be converted to parial function
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
            // This enumerator is not the last - add a 'flatMap' invocation and recursively translate the rest
            Apply(
              Select(adjustedTerm, Term.Name("flatMap")),
              List(Term.Function(List(param), translateFor(theNextEnumerators, body, finalFunctionName)))
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
