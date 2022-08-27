package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaModifiersResolverParams}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Mod.Final
import scala.meta.{Mod, Term}

trait TermParamTraverser extends ScalaTreeTraverser[Term.Param]

private[traversers] class TermParamTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                 typeTraverser: => TypeTraverser,
                                                 nameTraverser: => NameTraverser,
                                                 javaModifiersResolver: JavaModifiersResolver)
                                                (implicit javaWriter: JavaWriter) extends TermParamTraverser {

  import javaWriter._

  // method/lambda parameter declaration
  // Note that a primary ctor. param in Scala is also a class member which requires additional handling,
  // but that aspect will be handled by one of the parent traversers before this one is called
  override def traverse(termParam: Term.Param): Unit = {
    annotListTraverser.traverseMods(termParam.mods, onSameLine = true)
    writeModifiers(resolveJavaModifiers(termParam))
    termParam.decltpe.foreach(declType => {
      typeTraverser.traverse(declType)
      write(" ")
    })
    nameTraverser.traverse(termParam.name)
    // TODO handle 'default'
  }

  private def resolveJavaModifiers(termParam: Term.Param) = {
    val maybeAddedMod: Option[Mod] = javaScope match {
      // Can't add final in a Lambda param because it might not have an explicit type,
      // and we are not adding 'var' there either at this point since it has complicated rules
      case JavaTreeType.Lambda => None
      case _ => Some(Final())
    }
    val params = JavaModifiersResolverParams(
      scalaTree = termParam,
      scalaMods = termParam.mods :++ maybeAddedMod,
      javaTreeType = JavaTreeType.Parameter,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(params)
  }
}
