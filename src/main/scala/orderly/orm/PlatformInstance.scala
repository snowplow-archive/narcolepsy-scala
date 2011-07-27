package orderly.orm

// import orderly.api.PlatformInstance

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column

case class PlatformInstance(val id: Long,
                            @Column("platform_instance_name")
                            var platformInstanceName: String,
                            @Column("platform_id")
                            var platformId: Long,
                            var active: Boolean,
                            @Column("poll_frequency")
                            var pollFrequency: Int
                            ) extends KeyedEntity[Long] {
  def this() = this(0, "", 0, true, 0)

  def toJaxb: orderly.api.PlatformInstance = {

    val target = new orderly.api.PlatformInstance
    target.id = this.id
    target.platformInstanceName = this.platformInstanceName
    target.platformId = this.platformId
    target.active = this.active
    target.pollFrequency = this.pollFrequency

    return target
  }
}