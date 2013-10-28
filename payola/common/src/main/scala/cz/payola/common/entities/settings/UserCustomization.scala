package cz.payola.common.entities.settings

import scala.collection.immutable

/**
* Customization of a graph appearance based on an user configuration.
*/
trait UserCustomization extends DefinedCustomization
{
    override def classNameText = "user customization"

    /** URL that is used for customization. */
    val URLs: String

    /** Type of the class customizations in the ontology customization. */
    type ClassCustomizationType <: ClassCustomization

    protected var _classCustomizations: immutable.Seq[ClassCustomizationType]

    /** Customizations of classes in the ontology. */
    override def classCustomizations = _classCustomizations
}
