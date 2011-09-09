package orderly.apiclient

// Spray
import cc.spray._
import http._ // To get HttpRequest etc
import cc.spray.client._

class OrderlyApiClient(
  val requestUri:   String,
  val contentType:  String = "application/xml") {

  // TODO let's validate for supported contentTypes

  // Debug
  Console.println("Accessing API at %s using %s".format(requestUri, contentType))

  // Build a Spray http client with custom configuration options
  val client = new HttpClient(ClientConfig(
    requestTimeoutInMs = 300,
    userAgent = "orderly-scala-client v0.0.1",
    useRawUrl = false
  ))

  // TODO: let's add in the Accept header based on the contentType requested
  val request = new HttpRequest(
    method = HttpMethods.GET,
    uri = requestUri,
    headers = Nil,
    content = None,
    remoteHost = None,
    version = None
  )
  val future = client.dispatch(request)
  val response = future.get //()
  Console.println("Result of response: %s".format(response.toString))
}