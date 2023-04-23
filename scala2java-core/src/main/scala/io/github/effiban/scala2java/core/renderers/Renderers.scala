package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

class Renderers(implicit javaWriter: JavaWriter) {

  val bindRenderer: BindRenderer = new BindRendererImpl()

  val litRenderer: LitRenderer = new LitRendererImpl()

  private val nameIndeterminateRenderer: NameIndeterminateRenderer = new NameIndeterminateRendererImpl()

  val nameRenderer: NameRenderer = new NameRendererImpl(
    nameIndeterminateRenderer,
    termNameRenderer,
    typeNameRenderer
  )

  val patExtractRenderer: PatExtractRenderer = new PatExtractRendererImpl()

  val patInterpolateRenderer: PatInterpolateRenderer = new PatInterpolateRendererImpl()

  val patSeqWildcardRenderer: PatSeqWildcardRenderer = new PatSeqWildcardRendererImpl()

  val patTupleRenderer: PatTupleRenderer = new PatTupleRendererImpl()

  val patVarRenderer: PatVarRenderer = new PatVarRendererImpl(termNameRenderer)

  val patWildcardRenderer: PatWildcardRenderer = new PatWildcardRendererImpl()

  val selfRenderer: SelfRenderer = new SelfRendererImpl()

  lazy val termNameRenderer: TermNameRenderer = new TermNameRendererImpl()

  val termPlaceholderRenderer: TermPlaceholderRenderer = new TermPlaceholderRendererImpl()

  val typeAnonymousParamRenderer: TypeAnonymousParamRenderer = new TypeAnonymousParamRendererImpl()

  val typeApplyInfixRenderer: TypeApplyInfixRenderer = new TypeApplyInfixRendererImpl()

  val typeLambdaRenderer: TypeLambdaRenderer = new TypeLambdaRendererImpl()

  lazy val typeNameRenderer: TypeNameRenderer = new TypeNameRendererImpl()

  val typeVarRenderer: TypeVarRenderer = new TypeVarRendererImpl()
}
