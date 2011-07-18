package orderly.apiclient

import org.squeryl._
import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.PrimitiveTypeMode._

import orderly.orm._

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

  def getPlatformInstanceParameters(platformInstanceId: Long): Map[String, String] = {

    inTransaction {
      import ApplicationSchema._
      val parameters = from (platformInstanceParameters)(pip => where(pip.platform_instance_id === platformInstanceId) select(pip))
      parameters.map(p => (p.parameter_name -> p.parameter_value)).toMap
    }
  }
}