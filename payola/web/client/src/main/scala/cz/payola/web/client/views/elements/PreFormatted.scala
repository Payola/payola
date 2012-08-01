package cz.payola.web.client.views.elements

import s2js.adapters.js.dom
import cz.payola.web.client.views._
import cz.payola.web.client.View

class PreFormatted(content: String, cssClass: String = "")
    extends ElementView[dom.Element]("pre", List(new Text(content)), cssClass)
{
    override def render(parent: dom.Element) {
        parentElement = Some(parent)
        parent.appendChild(domElement)
        subViews.foreach { v =>
            v.render(domElement)
        }
    }
}
