package orderly.api

import orderly.orm._

import org.simpleframework.xml._

@Root
class PlatformInstance(
  @Element(name="Id")
  val id: Long,

  @Element(name="Name")
  val platformInstanceName: String,

  @Element(name="PlatformId")
  val platformId: Long,

  @Element(name="Active")
  val active: Boolean,

  @Element(name="PollFrequency")
  val pollFrequency: Int
) {
}

object PlatformInstance {
    // Test to instantiate one of these...
  def fromSqueryl(pi: orderly.orm.PlatformInstance): orderly.api.PlatformInstance = {
    val blah = new orderly.api.PlatformInstance(
      pi.id,
      pi.platformInstanceName,
      pi.platformId,
      pi.active,
      pi.pollFrequency
    )

    Console.println("%d %s %d %s %d".format(blah.id, blah.platformInstanceName, blah.platformId, blah.active, blah.pollFrequency))

    return blah
  }
}

