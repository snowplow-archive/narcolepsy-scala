package orderly.orm

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column

case class Platform(val id: Long,
                    @Column("platform_name")
                    var platformName: String,
                    @Column("platform_connector")
                    var platformConnector: String
                    ) extends KeyedEntity[Long] {
  def this() = this(0, "", "")
}