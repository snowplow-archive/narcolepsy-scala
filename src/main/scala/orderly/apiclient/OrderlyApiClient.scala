package orderly.apiclient

import orderly.orm._

import org.squeryl._
import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.PrimitiveTypeMode._

// TODO: obviously the API client will not really be initialised with
// TODO: a database connection string - but this should get us started
class OrderlyApiClient(val connString: String,
                       val dbUser: String,
                       val dbPassword: String) {

  // Initialise database connection
  Class.forName("org.postgresql.Driver")

  SessionFactory.concreteFactory = Some(()=>
    Session.create(
      java.sql.DriverManager.getConnection(connString, dbUser, dbPassword),
      new PostgreSqlAdapter))

  def getPlatformInstanceParameters(platformInstanceId: Long): List[PlatformInstanceParameter] = {

    inTransaction {
      import ApplicationSchema._
      val parameters = from (platformInstanceParameters)(pip => where(pip.platformInstanceId === platformInstanceId) select(pip))
      parameters.toList // Return in list form, List[PlatformInstanceParameter]
    }
  }

  def getPlatformInstances: List[PlatformInstance] = {

    inTransaction {
      import ApplicationSchema._
      val instances = from (platformInstances)(pi => select(pi))
      instances.toList // Return in list form, List[PlatformInstance]
    }
  }
}