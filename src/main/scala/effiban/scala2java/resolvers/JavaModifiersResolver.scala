package effiban.scala2java.resolvers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.JavaModifier
import effiban.scala2java.orderings.JavaModifierOrdering
import effiban.scala2java.transformers.ModifierTransformer

import scala.meta.Mod

trait JavaModifiersResolver {

  def resolve(context: JavaModifiersContext): List[JavaModifier]
}

class JavaModifiersResolverImpl(javaAllowedModifiersResolver: JavaAllowedModifiersResolver,
                                javaExtraModifiersResolver: JavaAllExtraModifiersResolver) extends JavaModifiersResolver {

  override def resolve(context: JavaModifiersContext): List[JavaModifier] = {
    import context._

    val modifierNamesBuilder = Set.newBuilder[JavaModifier]

    // Transform the Scala modifiers into corresponding Java modifiers, filtering allowed
    val allowedJavaModifiers = javaAllowedModifiersResolver.resolve(javaTreeType, javaScope)
    modifierNamesBuilder ++= transform(scalaMods, allowedJavaModifiers)

    // Add extra Java-specific modifiers which are required by the context
    modifierNamesBuilder ++= javaExtraModifiersResolver.resolve(context)

    modifierNamesBuilder.result()
      .toList
      .sorted(JavaModifierOrdering)
  }

  private def transform(inputScalaMods: List[Mod], allowedJavaModifiers: Set[JavaModifier]): List[JavaModifier] = {
    inputScalaMods
      .map(ModifierTransformer.transform)
      .collect { case Some(javaModifier) => javaModifier }
      .distinct
      .filter(allowedJavaModifiers.contains)
  }
}

object JavaModifiersResolver extends JavaModifiersResolverImpl(
  JavaAllowedModifiersResolver,
  JavaAllExtraModifiersResolver
)
