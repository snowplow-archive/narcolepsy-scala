package orderly.orm

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column

case class PlatformInstanceParameter(val id: Long,
                                     @Column("platform_instance_id")
                                     var platformInstanceId: Long,
                                     @Column("parameter_name")
                                     var parameterName: String,
                                     @Column("parameter_value")
                                     var parameterValue: String
                                     ) extends KeyedEntity[Long] {
  def this() = this(0, 0, "", "")
}