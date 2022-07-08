package effiban.scala2java.traversers

import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.{JavaEmitter, entities}

import scala.meta.Defn

trait ObjectTraverser extends ScalaTreeTraverser[Defn.Object]

private[scala2java] class ObjectTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                              templateTraverser: => TemplateTraverser,
                                              javaModifiersResolver: JavaModifiersResolver)
                                             (implicit javaEmitter: JavaEmitter) extends ObjectTraverser {

  import javaEmitter._

  override def traverse(objectDef: Defn.Object): Unit = {
    emitLine()
    emitComment("originally a Scala object")
    emitLine()
    annotListTraverser.traverseMods(objectDef.mods)
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForClass(objectDef.mods),
      typeKeyword = "class",
      name = s"${objectDef.name.toString}")
    val outerJavaScope = javaScope
    javaScope = entities.Class
    templateTraverser.traverse(objectDef.templ)
    javaScope = outerJavaScope
  }
}

object ObjectTraverser extends ObjectTraverserImpl(
  AnnotListTraverser,
  TemplateTraverser,
  JavaModifiersResolver)

