package co.orderly.orm

import org.squeryl.Schema

object ApplicationSchema extends Schema {
  val platformInstanceParameters = table[PlatformInstanceParameter]("platform_instance_parameters")
}