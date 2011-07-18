package orderly.orm

import org.squeryl.KeyedEntity

case class PlatformInstance(val id: Long,
                            var platform_instance_name: String,
                            var platform_id: Long,
                            var poll_frequency: Int
                            ) extends KeyedEntity[Long] {
  def this() = this(0, "", 0, 0)
}