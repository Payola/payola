package cz.payola.web.shared

import s2js.compiler._
import cz.payola.domain.entities.plugins.compiler.PluginCompiler
import cz.payola.domain.entities.plugins.PluginClassLoader
import cz.payola.domain.entities.User

@remote object PluginManager
{
    @async @secured def uploadPlugin(pluginCode: String, user: User = null)(successCallback: (String => Unit))(failCallback: (Throwable => Unit)) {
        // Sanity check
        assert(user != null, "Not logged in, or some other error")

        // Try to compile code
        val libDirectory = new java.io.File("lib")
        val pluginClassDirectory = new java.io.File("plugins")
        if (!pluginClassDirectory.exists()){
            pluginClassDirectory.mkdir()
        }

        val compiler = new PluginCompiler(libDirectory, pluginClassDirectory)
        try {
            val className = compiler.compile(pluginCode)
            val loader = new PluginClassLoader(pluginClassDirectory, getClass.getClassLoader)
            val plugin = loader.instantiatePlugin(className)

            if (Payola.model.pluginModel.getByName(plugin.name).isDefined) {
                failCallback(new Exception("Plugin with this name already exists!"))
            }else{
                plugin.owner = Some(user)
                user.addOwnedPlugin(plugin)
                Payola.model.pluginModel.persist(plugin)

                successCallback("Plugin saved.")
            }
        }catch {
            case e: Exception => {
                e.printStackTrace()
                failCallback(new Exception("Code couldn't be compiled or loaded. \n\nDetails: " + e.getMessage))
            }
        }





    }


}
