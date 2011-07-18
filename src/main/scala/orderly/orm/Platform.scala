package orderly.orm

import org.squeryl.KeyedEntity

case class Platform(val id: Long,
                    var platform_name: String,
                    var platform_value: String
                    ) extends KeyedEntity[Long] {
  def this() = this(0, "", "")
}