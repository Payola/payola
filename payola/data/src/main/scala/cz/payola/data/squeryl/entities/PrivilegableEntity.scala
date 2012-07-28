package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl._

/**
  * An entity that may be granted privileges.
  */
trait PrivilegableEntity extends Entity with cz.payola.domain.entities.PrivilegableEntity
{
    _privileges = null

    implicit val context: SquerylDataContextComponent

    override def privileges = {
        if (_privileges == null) {
            _privileges = context.privilegeRepository.getAllByGranteeId(id).toBuffer
        }

        _privileges.toList
    }

    override def storePrivilege(privilege: PrivilegeType) {
        // Call domain method to preserve functionality
        super.storePrivilege(context.privilegeRepository.persist(privilege))
    }


    override def discardPrivilege(privilege: PrivilegeType) {
        // Call domain method to preserve functionality
        super.discardPrivilege(privilege)

        context.privilegeRepository.removeById(privilege.id)
    }
}
