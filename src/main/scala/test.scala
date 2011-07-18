// import java.io.Console
import orderly.apiclient._

object Test {

  def main(args: Array[String]) {
    val api = new OrderlyApiClient("jdbc:postgresql://localhost:5432/ord_erp_dev", "postgres", "postgres")
    val prestashopParams = api.getPlatformInstanceParameters(1) // Get the PrestaShop API parameters
    val mwsParams = api.getPlatformInstanceParameters(2) // Get the Amazon MWS API parameters

    val instances = api.getPlatformInstances
    instances.foreach(i => Console.println("platform instance %d (%s) should be polled every %d secs".format(i.id, i.platformInstanceName, i.pollFrequency)))
  }
}