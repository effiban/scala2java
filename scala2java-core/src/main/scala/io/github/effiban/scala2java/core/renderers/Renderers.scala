package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

class Renderers(implicit javaWriter: JavaWriter) {

  lazy val alternativeRenderer: AlternativeRenderer = new AlternativeRendererImpl(patRenderer)

  val argumentListRenderer: ArgumentListRenderer = new ArgumentListRendererImpl()

  lazy val defaultTermRefRenderer: DefaultTermRefRenderer = new DefaultTermRefRendererImpl(
    thisRenderer,
    superRenderer,
    termNameRenderer,
    defaultTermSelectRenderer
  )

  lazy val defaultTermSelectRenderer: DefaultTermSelectRenderer = new DefaultTermSelectRendererImpl(
    defaultTermRefRenderer,
    termNameRenderer
  )

  val litRenderer: LitRenderer = new LitRendererImpl()

  val nameIndeterminateRenderer: NameIndeterminateRenderer = new NameIndeterminateRendererImpl()

  val nameRenderer: NameRenderer = new NameRendererImpl(
    nameIndeterminateRenderer,
    termNameRenderer,
    typeNameRenderer
  )

  lazy val patRenderer: PatRenderer = new PatRendererImpl(
    litRenderer,
    termNameRenderer,
    patWildcardRenderer,
    patVarRenderer,
    alternativeRenderer
  )

  val patVarRenderer: PatVarRenderer = new PatVarRendererImpl(termNameRenderer)

  val patWildcardRenderer: PatWildcardRenderer = new PatWildcardRendererImpl()

  val selfRenderer: SelfRenderer = new SelfRendererImpl()

  val superRenderer: SuperRenderer = new SuperRendererImpl(nameRenderer)

  lazy val termNameRenderer: TermNameRenderer = new TermNameRendererImpl()

  val termPlaceholderRenderer: TermPlaceholderRenderer = new TermPlaceholderRendererImpl()

  val thisRenderer: ThisRenderer = new ThisRendererImpl(nameRenderer)

  lazy val typeAnnotateRenderer: TypeAnnotateRenderer = new TypeAnnotateRendererImpl(typeRenderer)

  val typeAnonymousParamRenderer: TypeAnonymousParamRenderer = new TypeAnonymousParamRendererImpl()

  val typeApplyInfixRenderer: TypeApplyInfixRenderer = new TypeApplyInfixRendererImpl()

  lazy val typeApplyRenderer: TypeApplyRenderer = new TypeApplyRendererImpl(typeRenderer, typeListRenderer)

  lazy val typeBoundsRenderer: TypeBoundsRenderer = new TypeBoundsRendererImpl(typeRenderer)

  lazy val typeExistentialRenderer: TypeExistentialRenderer = new TypeExistentialRendererImpl(typeRenderer)

  lazy val typeListRenderer: TypeListRenderer = new TypeListRendererImpl(
    argumentListRenderer,
    new SimpleArgumentRenderer(typeRenderer)
  )

  lazy val typeNameRenderer: TypeNameRenderer = new TypeNameRendererImpl()

  lazy val typeProjectRenderer: TypeProjectRenderer = new TypeProjectRendererImpl(
    typeRenderer,
    typeNameRenderer
  )

  lazy val typeRefRenderer: TypeRefRenderer = new TypeRefRendererImpl(
    typeNameRenderer,
    typeSelectRenderer,
    typeProjectRenderer,
    typeSingletonRenderer
  )

  lazy val typeRefineRenderer: TypeRefineRenderer = new TypeRefineRendererImpl(typeRenderer)

  lazy val typeRenderer: TypeRenderer = new TypeRendererImpl(
    typeRefRenderer,
    typeApplyRenderer,
    typeApplyInfixRenderer,
    typeWithRenderer,
    typeRefineRenderer,
    typeExistentialRenderer,
    typeAnnotateRenderer,
    typeAnonymousParamRenderer,
    typeWildcardRenderer,
    typeRepeatedRenderer,
    typeVarRenderer
  )

  lazy val typeRepeatedRenderer: TypeRepeatedRenderer = new TypeRepeatedRendererImpl(typeRenderer)

  lazy val typeSelectRenderer: TypeSelectRenderer = new TypeSelectRendererImpl(defaultTermRefRenderer, typeNameRenderer)

  val typeSingletonRenderer: TypeSingletonRenderer = new TypeSingletonRendererImpl(thisRenderer)

  val typeVarRenderer: TypeVarRenderer = new TypeVarRendererImpl()

  lazy val typeWildcardRenderer: TypeWildcardRenderer = new TypeWildcardRendererImpl(typeBoundsRenderer)

  lazy val typeWithRenderer: TypeWithRenderer = new TypeWithRendererImpl(typeRenderer)
}
