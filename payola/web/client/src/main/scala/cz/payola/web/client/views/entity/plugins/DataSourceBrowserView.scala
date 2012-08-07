package cz.payola.web.client.views.entity.plugins

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements.form.fields._
import scala.Some

class DataSourceBrowserView(dataSourceName: String) extends ComposedView
{
    val heading = new Heading(List(new Text("Data source: " + dataSourceName)), 2)

    val backButton = new Button(new Icon(Icon.arrow_left))

    val nextButton = new Button(new Icon(Icon.arrow_right))

    val nodeUriInput = new TextInput("nodeUri", "", "Node URI", "input-xlarge")

    val goButton = new Button(new Text("Go!"))

    val sparqlQueryButton = new Button(new Text("SPARQL"), "", new Icon(Icon.asterisk))

    val navigation = new Div(List(
        backButton,
        nextButton,
        nodeUriInput,
        goButton,
        sparqlQueryButton),
        "form-inline pull-right"
    )

    val graphViewSpace = new Div(Nil, "row-fluid")

    def createSubViews = {
        List(
            new Div(List(
                new Div(List(heading), "span4"),
                new Div(List(navigation), "span8")),
                "row-fluid"
            ),
            graphViewSpace
        )
    }
}
