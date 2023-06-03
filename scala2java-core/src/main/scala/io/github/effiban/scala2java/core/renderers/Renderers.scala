package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.classifiers.JavaStatClassifier
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Assign

class Renderers(implicit javaWriter: JavaWriter) {

  lazy val alternativeRenderer: AlternativeRenderer = new AlternativeRendererImpl(patRenderer)

  lazy val applyTypeRenderer: ApplyTypeRenderer = new ApplyTypeRendererImpl(classOfRenderer)

  lazy val applyUnaryRenderer: ApplyUnaryRenderer = new ApplyUnaryRendererImpl(termNameRenderer, expressionTermRenderer)

  val argumentListRenderer: ArgumentListRenderer = new ArgumentListRendererImpl()

  lazy val assignInvocationArgRenderer: InvocationArgRenderer[Assign] = new AssignInvocationArgRenderer(
    assignLHSRenderer,
    expressionTermRenderer
  )

  lazy val assignLHSRenderer: AssignLHSRenderer = new AssignLHSRendererImpl(expressionTermRenderer)

  lazy val blockRenderer: BlockRenderer = new BlockRendererImpl(blockTermRenderer, initRenderer)

  lazy val blockTermRenderer: BlockTermRenderer = new BlockTermRendererImpl(
    expressionTermRefRenderer,
    defaultTermRenderer,
    JavaStatClassifier
  )

  lazy val classOfRenderer: ClassOfRenderer = new ClassOfRendererImpl(typeRenderer)

  lazy val compositeInvocationArgRenderer: InvocationArgRenderer[Term] = new CompositeInvocationArgRenderer(
    assignInvocationArgRenderer,
    expressionTermRenderer
  )

  lazy val defaultTermRefRenderer: DefaultTermRefRenderer = new DefaultTermRefRendererImpl(
    thisRenderer,
    superRenderer,
    termNameRenderer,
    defaultTermSelectRenderer
  )

  lazy val defaultTermRenderer: DefaultTermRenderer = new DefaultTermRendererImpl(
    defaultTermRefRenderer,
    applyTypeRenderer,
    blockRenderer,
    ifRenderer,
    litRenderer
  )

  lazy val defaultTermSelectRenderer: DefaultTermSelectRenderer = new DefaultTermSelectRendererImpl(
    defaultTermRefRenderer,
    termNameRenderer
  )

  lazy val expressionTermRenderer: ExpressionTermRenderer = new ExpressionTermRendererImpl(
    expressionTermRefRenderer,
    ifRenderer,
    defaultTermRenderer
  )

  lazy val expressionTermRefRenderer: ExpressionTermRefRenderer = new ExpressionTermRefRendererImpl(
    expressionTermSelectRenderer,
    applyUnaryRenderer,
    defaultTermRefRenderer
  )

  lazy val expressionTermSelectRenderer: ExpressionTermSelectRenderer = new ExpressionTermSelectRendererImpl(
    expressionTermRenderer,
    typeListRenderer,
    termNameRenderer
  )

  lazy val ifRenderer: IfRenderer = new IfRendererImpl(
    expressionTermRenderer,
    blockRenderer,
    defaultTermRenderer
  )

  lazy val importeeRenderer: ImporteeRenderer = new ImporteeRendererImpl(nameIndeterminateRenderer)

  lazy val importRenderer: ImportRenderer = new ImportRendererImpl(importerRenderer)

  lazy val importerRenderer: ImporterRenderer = new ImporterRendererImpl(defaultTermRefRenderer, importeeRenderer)

  lazy val initRenderer: InitRenderer = new InitRendererImpl(
    typeRenderer,
    argumentListRenderer,
    compositeInvocationArgRenderer
  )

  val litRenderer: LitRenderer = new LitRendererImpl()

  lazy val nameIndeterminateRenderer: NameIndeterminateRenderer = new NameIndeterminateRendererImpl()

  val nameRenderer: NameRenderer = new NameRendererImpl(
    nameIndeterminateRenderer,
    termNameRenderer,
    typeNameRenderer
  )

  lazy val patListRenderer: PatListRenderer = new PatListRendererImpl(
    argumentListRenderer,
    new SimpleArgumentRenderer(patRenderer)
  )

  lazy val patRenderer: PatRenderer = new PatRendererImpl(
    litRenderer,
    termNameRenderer,
    patWildcardRenderer,
    patVarRenderer,
    alternativeRenderer,
    patTypedRenderer
  )

  val patVarRenderer: PatVarRenderer = new PatVarRendererImpl(termNameRenderer)

  lazy val patTypedRenderer: PatTypedRenderer = new PatTypedRendererImpl(typeRenderer, patRenderer)

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
