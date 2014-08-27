package cz.payola.web.client.views.datacube

import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.graph.PluginView
import s2js.compiler.javascript

class ClientDataCubeVisualizer(prefixApplier: Option[PrefixApplier] = None)
    extends PluginView[String]("DataCube", prefixApplier)
{
    def supportedDataFormat: String = "RDF/JSON"

    @javascript(
        """ console.log("red"); location.href = '/visualize/datacube/'+evaluationId; """)
    def redirect(evaluationId: String) {}

    def createSubViews = {
        List()
    }

    def isAvailable(availableTransformators: List[String], evaluationId: String, success: () => Unit, fail: () => Unit) {
        success()
    }

    override def loadDefaultCachedGraph(evaluationId: String, updateGraph: Option[String] => Unit) {
        redirect(evaluationId)
    }
}
