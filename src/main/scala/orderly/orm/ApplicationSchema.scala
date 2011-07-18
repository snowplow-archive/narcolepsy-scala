package orderly.orm

import org.squeryl.Schema

object ApplicationSchema extends Schema {
  val platforms = table[Platform]("platforms")
  val platformInstances = table[PlatformInstance]("platform_instances")
  val platformInstanceParameters = table[PlatformInstanceParameter]("platform_instance_parameters")
}