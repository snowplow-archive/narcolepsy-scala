package orderly.orm

import org.squeryl.KeyedEntity

case class PlatformInstanceParameter(val id: Long,
                                     var platform_instance_id: Long,
                                     var parameter_name: String,
                                     var parameter_value: String
                                     ) extends KeyedEntity[Long] {
  def this() = this(0, 0, "", "")
}