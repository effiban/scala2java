package effiban.scala2java.traversers

import effiban.scala2java.contexts.{BlockContext, DefnDefContext, JavaModifiersContext, StatContext}
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.Method
import effiban.scala2java.entities.TraversalConstants.UnknownType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.typeinference.TermTypeInferrer
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Defn, Init, Type}

trait DefnDefTraverser {
  def traverse(defnDef: Defn.Def, context: DefnDefContext = DefnDefContext()): Unit
}

private[traversers] class DefnDefTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeParamListTraverser: => TypeParamListTraverser,
                                               termNameTraverser: => TermNameTraverser,
                                               typeTraverser: => TypeTraverser,
                                               termParamListTraverser: => TermParamListTraverser,
                                               blockTraverser: => BlockTraverser,
                                               termTypeInferrer: => TermTypeInferrer,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaWriter: JavaWriter) extends DefnDefTraverser {

  import javaWriter._

  override def traverse(defnDef: Defn.Def, context: DefnDefContext = DefnDefContext()): Unit = {
    writeLine()
    annotListTraverser.traverseMods(defnDef.mods)
    writeModifiers(resolveJavaModifiers(defnDef))
    traverseTypeParams(defnDef.tparams)
    traverseMethodType(defnDef)
    termNameTraverser.traverse(defnDef.name)

    val outerJavaScope = javaScope
    javaScope = Method
    traverseMethodParamsAndBody(defnDef, context.maybeInit)
    javaScope = outerJavaScope
  }

  private def traverseMethodParamsAndBody(defDef: Defn.Def, maybeInit: Option[Init] = None): Unit = {
    termParamListTraverser.traverse(termParams = defDef.paramss.flatten, context = StatContext(Method))
    val withReturnValue = defDef.decltpe match {
      case Some(Type.Name("Unit")) => false
      case Some(Type.AnonymousName()) => false
      case Some(_) => true
      // Taking a "reasonable" chance here - if the Scala method has no declared type and inferred type is void,
      // there will be an incorrect 'return' (as opposed to the opposite case when it would be missing)
      case None => true
    }
    val blockContext = BlockContext(shouldReturnValue = withReturnValue, maybeInit = maybeInit)
    blockTraverser.traverse(stat = defDef.body, context = blockContext)
  }

  private def traverseTypeParams(tparams: List[Type.Param]): Unit = {
    tparams match {
      case Nil =>
      case typeParams =>
        typeParamListTraverser.traverse(typeParams)
        write(" ")
    }
  }

  private def traverseMethodType(defnDef: Defn.Def): Unit = {
    defnDef.decltpe match {
      case Some(Type.AnonymousName()) =>
      case Some(tpe) =>
        typeTraverser.traverse(tpe)
        write(" ")
      case None =>
        termTypeInferrer.infer(defnDef.body) match {
          case Some(tpe) => typeTraverser.traverse(tpe)
          case None => writeComment(UnknownType)
        }
        write(" ")
    }
  }

  private def resolveJavaModifiers(defnDef: Defn.Def) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = defnDef,
      scalaMods = defnDef.mods,
      javaTreeType = JavaTreeType.Method,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}
