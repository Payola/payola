package cz.payola.web.shared

import cz.payola.domain.entities.analyses.evaluation._
import scala.collection.mutable.HashMap
import s2js.compiler.async
import cz.payola.model.ModelException

// TODO move the logic to the model.
@remote object AnalysisRunner
{
    val runningEvaluations : HashMap[String, AnalysisEvaluation] = new HashMap[String, AnalysisEvaluation]

    @async def runAnalysisById(id: String)(successCallback: (String => Unit))(failCallback: (Throwable => Unit)) = {
        val analysisOpt = Payola.model.analysisModel.getById(id)

        if (analysisOpt.isEmpty) {
            throw new ModelException("The analysis doesn't exist.") // TODO
        }

        runningEvaluations.put(id, analysisOpt.get.evaluate())

        successCallback(id)
    }

    @async def getAnalysisProgress(evaluationId: String)(successCallback: (AnalysisProgress => Unit))(failCallback: (Throwable => Unit)) = {

        val evaluation = runningEvaluations.get(evaluationId).get
        val progress = evaluation.getProgress

        val evaluated = progress.evaluatedInstances.map(i => i.id)
        val running = progress.runningInstances.map(m => m._1.id).toList
        val errors = progress.errors.map(tuple => tuple._1.id).toList

        if (evaluation.isFinished)
        {
            runningEvaluations -= evaluationId
        }

        val graph = evaluation.getResult.flatMap{
            case r: Success => Some(r.outputGraph)
            case _ => None
        }

        successCallback(new AnalysisProgress(evaluated, running, errors, progress.value, evaluation.isFinished, graph))
    }
}
