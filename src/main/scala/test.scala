import orderly.apiclient._

object Test {

  def main(args: Array[String]) {
    val api = new OrderlyApiClient("http://localhost:8080/products")
  }
}