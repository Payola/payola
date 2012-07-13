package cz.payola.web.client.views.plugins.visual

import s2js.adapters.js.dom._
import cz.payola.web.client._
import cz.payola.web.client.views.Vector2D

/**
  * Representation of a space for drawing into a web page.
  * @param canvas object specifying sizes of the context
  * @param context object for drawing
  */
class Layer(val canvas: Canvas, val context: CanvasRenderingContext2D) //TODO delete
{

    /**
      * Indicator whether is the layer ready for drawing into.
      */
    var cleared = false

    /**
      * Setter of canvas dimensions.
      * @param size new dimensions.
      */
    def setSize(size: Vector2D) {
        canvas.width = size.x;
        canvas.height = size.y;
    }

    /**
      * Getter of canvas dimensions.
      * @return dimensions
      */
    def getSize: Vector2D = {
        Vector2D(canvas.width, canvas.height)
    }
}
