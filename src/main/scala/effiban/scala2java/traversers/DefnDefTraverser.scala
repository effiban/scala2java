package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.JavaScope.{Interface, Method}
import effiban.scala2java.entities.TraversalConstants.UnknownType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.Block
import scala.meta.{Defn, Init, Term, Type}

trait DefnDefTraverser {
  def traverse(defnDef: Defn.Def, maybeInit: Option[Init] = None): Unit
}

private[traversers] class DefnDefTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               termNameTraverser: => TermNameTraverser,
                                               typeTraverser: => TypeTraverser,
                                               termParamListTraverser: => TermParamListTraverser,
                                               blockTraverser: => BlockTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DefnDefTraverser {

  import javaWriter._

  override def traverse(defnDef: Defn.Def, maybeInit: Option[Init] = None): Unit = {
    writeLine()
    annotListTraverser.traverseMods(defnDef.mods)
    val resolvedModifierNames = javaScope match {
      case Interface => javaModifiersResolver.resolveForInterfaceMethod(defnDef.mods, hasBody = true)
      case JavaScope.Class => javaModifiersResolver.resolveForClassMethod(defnDef.mods)
      case _ => Nil
    }
    writeModifiers(resolvedModifierNames)
    defnDef.decltpe match {
      case Some(Type.AnonymousName()) =>
      case Some(tpe) =>
        typeTraverser.traverse(tpe)
        write(" ")
      case None =>
        writeComment(UnknownType)
        write(" ")
    }
    termNameTraverser.traverse(defnDef.name)
    //TODO handle method type params

    val outerJavaScope = javaScope
    javaScope = Method
    traverseMethodParamsAndBody(defnDef, maybeInit)
    javaScope = outerJavaScope
  }

  private def traverseMethodParamsAndBody(defDef: Defn.Def, maybeInit: Option[Init] = None): Unit = {
    termParamListTraverser.traverse(defDef.paramss.flatten)
    val withReturnValue = defDef.decltpe match {
      case Some(Type.Name("Unit")) => false
      case Some(Type.AnonymousName()) => false
      case Some(_) => true
      // Taking a "reasonable" chance here - if the Scala method has no declared type and inferred type is void,
      // there will be an incorrect 'return' (as opposed to the opposite case when it would be missing)
      case None => true
    }
    val block = defDef.body match {
      case blk: Block => blk
      case term: Term => Block(List(term))
    }
    blockTraverser.traverse(block = block,
        shouldReturnValue = withReturnValue,
        maybeInit = maybeInit)
  }
}
