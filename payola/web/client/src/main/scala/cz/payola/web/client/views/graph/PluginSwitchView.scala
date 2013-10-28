package cz.payola.web.client.views.graph

import cz.payola.common.rdf._
import cz.payola.common.entities.settings._
import cz.payola.web.client.views._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.graph.table._
import cz.payola.web.client.views.graph.visual._
import cz.payola.web.client.views.graph.visual.techniques.circle.CircleTechnique
import cz.payola.web.client.views.graph.visual.techniques.tree.TreeTechnique
import cz.payola.web.client.views.graph.visual.techniques.gravity.GravityTechnique
import cz.payola.web.shared.managers._
import cz.payola.web.client.events._
import cz.payola.web.client.views.elements.lists.ListItem
import cz.payola.web.client.views.graph.sigma.GraphSigmaPluginView
import cz.payola.web.client.views.graph.datacube.TimeHeatmap
import cz.payola.web.client.models.PrefixApplier
import scala.Some
import cz.payola.web.shared.AnalysisEvaluationResultsManager
import scala.Some

class PluginSwitchView(prefixApplier: PrefixApplier) extends GraphView with ComposedView
{
    /**
     * Event triggered when ontology customization is created.
     */
    val ontologyCustomizationCreateClicked = new SimpleUnitEvent[this.type]

    /**
     * Event triggered when ontology customization is selected.
     */
    val ontologyCustomizationSelected = new SimpleUnitEvent[OntologyCustomization]

    /**
     * Event triggered when ontology customization is edited.
     */
    val ontologyCustomizationEditClicked = new SimpleUnitEvent[OntologyCustomization]

    /**
     * Event triggered when user customization is selected.
     */
    val userCustomizationSelected = new SimpleUnitEvent[UserCustomization]

    /**
     * Event triggered when user customization is created.
     */
    val userCustomizationCreateClicked = new SimpleUnitEvent[this.type]

    /**
     * Event triggered when user customization is edited.
     */
    val userCustomizationEditClicked = new SimpleUnitEvent[UserCustomization]

    /**
     * List of available visualization plugins.
     */
    private val plugins = List[PluginView](
        new TripleTablePluginView(Some(prefixApplier)),
        new SelectResultPluginView(Some(prefixApplier)),
        new CircleTechnique(Some(prefixApplier)),
        new TreeTechnique(Some(prefixApplier)),
        new GravityTechnique(Some(prefixApplier)),
        new ColumnChartPluginView(Some(prefixApplier)),
        new GraphSigmaPluginView(Some(prefixApplier)),
        new TimeHeatmap(Some(prefixApplier))
    )

    /**
     * Currently used visualization plugin.
     */
    private var currentPlugin = plugins.head

    /**
     * Parent to the visualization plugin View object.
     */
    private val pluginSpace = new Div(Nil, "plugin-space")

    /**
     * Drop down button for selection of graph visualization.
     */
    val pluginChangeButton: DropDownButton = new DropDownButton(List(
        new Icon(Icon.eye_open),
        new Text("Change visualization plugin")),
        plugins.map { plugin =>
            val pluginAnchor = new Anchor(List(new Text(plugin.name)))
            val listItem = new ListItem(List(pluginAnchor))
            pluginAnchor.mouseClicked += { e =>
                pluginChangeButton.setActiveItem(listItem)
                changePlugin(plugin)
                false
            }
            listItem
        }
    )

    /**
     * Drop down button for selection of customization.
     */
    val customizationsButton = new DropDownButton(List(
        new Icon(Icon.wrench),
        new Text("Change appearance")),
        Nil
    )

    val languagesButton = new DropDownButton(
        List(new Icon(Icon.globe), new Text("Language")),
        Nil,
        "", "pull-right"
    ).setAttribute("style", "margin: 0 5px;")

    /**
     * Toolbar containing pluginChange, customization buttons
     */
    val toolbar = new Div(List(pluginChangeButton, customizationsButton, languagesButton), "btn-toolbar").setAttribute(
        "style", "margin-bottom: 15px;")

    // Re-trigger all events when the corresponding events are triggered in the plugins.
    plugins.foreach { plugin =>
        plugin.vertexSelected += { e => vertexSelected.trigger(createVertexEventArgs(e.vertex))}
        plugin.vertexBrowsing += { e => vertexBrowsing.trigger(createVertexEventArgs(e.vertex))}
        plugin.vertexSetMain += { e => vertexSetMain.trigger(createVertexEventArgs(e.vertex))}
        plugin.vertexBrowsingDataSource += { e => vertexBrowsingDataSource.trigger(createVertexEventArgs(e.vertex))}
    }

    // Display the first plugin.
    pluginChangeButton.setActiveItem(pluginChangeButton.items.head)
    currentPlugin.render(pluginSpace.htmlElement)
    currentPlugin.renderControls(toolbar.htmlElement)

    def createSubViews = List(toolbar, pluginSpace)

    override def update(graph: Option[Graph], customization: Option[DefinedCustomization]) {
        super.update(graph, customization)
        currentPlugin.setEvaluationId(evaluationId)
        currentPlugin.update(graph, customization)
    }

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean) {

        super.updateGraph(graph, contractLiterals)
        currentPlugin.setEvaluationId(evaluationId)
        currentPlugin.updateGraph(graph, contractLiterals)
    }

    override def updateCustomization(customization: Option[DefinedCustomization]) {
        super.updateCustomization(customization)
        currentPlugin.updateCustomization(customization)
    }

    override def setMainVertex(vertex: Vertex) {
        currentPlugin.setMainVertex(vertex)
    }

    override def setLanguage(language: Option[String]) {}


    /**
     * Updates the list of ontology customizations showed in the ontologyCustomizationButton drop-down button.
     * @param customizations customizations to set to the drop-down button
     */
    def updateAvailableCustomizations(userCustomizations: UserCustomizationsByOwnership,
        ontoCustomizations: OntologyCustomizationsByOwnership) {

        // The owned customizations that are editable.
        val ownedOnto = ontoCustomizations.ownedCustomizations.getOrElse(Nil).map(oo =>
            createOntologyCustomizationListItem(oo, true))
        val ownedUser = userCustomizations.ownedCustomizations.getOrElse(Nil).map(ou =>
            createUserCustomizationListItem(ou, true))

        // The customizations of other users.
        val othersOnto = ontoCustomizations.othersCustomizations.map(oo =>
            createOntologyCustomizationListItem(oo, false))
        val othersUser = userCustomizations.othersCustomizations.map(ou =>
            createUserCustomizationListItem(ou, false))

        // A separator between owned and others customizations.
        val separator1 = if ((ownedOnto.nonEmpty || ownedUser.nonEmpty) && (othersOnto.nonEmpty || othersUser.nonEmpty))
            List(new ListItem(Nil, "divider")) else Nil

        // The create new ontology based button.
        val createButtonByOntologyCustomization =
            new Anchor(List(new Icon(Icon.plus), new Text("Create New Ontology Customization")))
        createButtonByOntologyCustomization.mouseClicked += { e =>
            ontologyCustomizationCreateClicked.triggerDirectly(this)
            false
        }

        // The create new user defined customization button.
        val createButtonByUserCustomization =
            new Anchor(List(new Icon(Icon.plus), new Text("Create New User Customization")))
        createButtonByUserCustomization.mouseClicked += { e =>
            userCustomizationCreateClicked.triggerDirectly(this)
            false
        }

        val createNewCustomization =
            if(userCustomizations.ownedCustomizations.isDefined || ontoCustomizations.ownedCustomizations.isDefined) {
                val separator2 =
                    if ((ownedOnto.nonEmpty || ownedUser.nonEmpty) && (othersOnto.nonEmpty || othersUser.nonEmpty))
                        List(new ListItem(Nil, "divider")) else Nil
                separator2 ++ List(
                    new ListItem(List(createButtonByOntologyCustomization)),
                    new ListItem(List(createButtonByUserCustomization)))
            } else { Nil }

        // All the items merged together.
        val allItems = ownedOnto ++ ownedUser ++ separator1 ++ othersOnto ++ othersUser ++ createNewCustomization
        val items = if (allItems.nonEmpty) {
            allItems
        } else {
            val listItem = new ListItem(List(new Text("No settings available")))
            listItem.setAttribute("style", "padding-left: 10px;")
            List(listItem)
        }

        customizationsButton.items = items
    }

    def updateLanguages(languagesList: Seq[String]) {
        val listItems = languagesList.map { language =>
            val langText = new Text(language)
            val langListItem = new ListItem(List(new Anchor(List(langText))))
            langListItem.mouseClicked += { _ =>
                setLanguage(Some(langText.text)) //TODO
                languagesButton.setActiveItem(langListItem)
                false
            }
            langListItem
        }
        languagesButton.items = listItems
    }

    /**
     * Creates a single item for the ontologyCustomizationButton drop-down button.
     * @param customization that the created listItem will represent
     * @param isEditable if true the Edit button will be added to the created listItem
     * @return listItem representing the ontology customization
     */
    private def createOntologyCustomizationListItem(customization: OntologyCustomization, isEditable: Boolean): ListItem = {
        val editButton = new Button(new Text("Edit"), "btn-mini", new Icon(Icon.pencil)).setAttribute(
            "style", "position: absolute; right: 5px;")
        val anchor = new Anchor(List(new Text(customization.name)) ++ (if (isEditable) List(editButton) else Nil))

        val listItem = new ListItem(List(anchor))
        if (currentCustomization.exists(_ == customization)) {
            listItem.addCssClass("active")
        }

        anchor.mouseClicked += { e =>
            ontologyCustomizationSelected.triggerDirectly(customization)
            customizationsButton.setActiveItem(listItem)
            false
        }

        editButton.mouseClicked += { e =>
            ontologyCustomizationEditClicked.triggerDirectly(customization)
            false
        }

        listItem
    }

    private def createUserCustomizationListItem(customization: UserCustomization, isEditable: Boolean): ListItem = {
        val editButton = new Button(new Text("Edit"), "btn-mini", new Icon(Icon.pencil)).setAttribute(
            "style", "position: absolute; right: 5px;")
        val anchor = new Anchor(List(new Text(customization.name)) ++ (if (isEditable) List(editButton) else Nil))

        val listItem = new ListItem(List(anchor))
        if (currentCustomization.exists(_ == customization)) {
            listItem.addCssClass("active")
        }

        anchor.mouseClicked += { e =>
            userCustomizationSelected.triggerDirectly(customization)
            customizationsButton.setActiveItem(listItem)
            false
        }

        editButton.mouseClicked += { e =>
            userCustomizationEditClicked.triggerDirectly(customization)
            false
        }

        listItem
    }

    /**
     * Visualization plugin setter.
     * @param plugin plugin to set
     */
    private def changePlugin(plugin: PluginView) {
        if (currentPlugin != plugin) {
            // Destroy the current plugin.
            currentPlugin.update(None, None)
            currentPlugin.destroyControls()
            currentPlugin.destroy()

            // Switch to the new plugin.

            currentPlugin = plugin
            currentPlugin.setEvaluationId(None)
            currentPlugin.render(pluginSpace.htmlElement)
            currentPlugin.renderControls(toolbar.htmlElement)
            //the default visualization is TripleTableView, which has implemented a server-side caching, support for other visualizations will be added with transformation layer
            //now the whole graph has to fetched, this will be taken care of in transformation layer in next cache release iteration
            if(evaluationId.isDefined) {
                AnalysisEvaluationResultsManager.getCompleteAnalysisResult(evaluationId.get) { g =>
                    currentGraph = g
                    update(g, currentCustomization)
                    currentPlugin.drawGraph()
                } { err => }
            }
        }
    }

    private def createVertexEventArgs(vertex: Vertex): VertexEventArgs[this.type] = {
        new VertexEventArgs[this.type](this, vertex)
    }

    def getCurrentGraph = this.currentGraph

    def getCurrentGraphView = currentPlugin match {
        case visual: VisualPluginView =>
            visual.graphView
        case _ => None
    }
}
