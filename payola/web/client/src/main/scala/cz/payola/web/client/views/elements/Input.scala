package cz.payola.web.client.views.elements

import cz.payola.web.client.views._
import s2js.adapters.js.dom
import cz.payola.web.client.events.BrowserEvent

class Input(name: String, initialValue: String, title: Option[String], cssClass: String = "")
    extends ElementView[dom.Input]("input", Nil, cssClass)
{
    val changed = new BrowserEvent[Input]

    domElement.onkeyup = { e => changed.triggerDirectly(this, e) }

    value = initialValue
    setAttribute("name", name)
    setAttribute("id", name)
    setAttribute("type", "text")
    title.map { t =>
        setAttribute("placeholder", t)
        setAttribute("title", t)
    }

    def maxLength_=(maxLength: Int) {
        setAttribute("maxlength", maxLength.toString())
    }

    def maxLength: Int = {
        getAttribute("maxlength").toInt
    }

    def value: String = {
        domElement.value
    }

    def value_=(value: String) {
        domElement.value = value
    }

    def setIsActive(isActive: Boolean = true){
        if (isActive) {
            addCssClass("active")
        } else {
            removeCssClass("active")
        }
    }
}
