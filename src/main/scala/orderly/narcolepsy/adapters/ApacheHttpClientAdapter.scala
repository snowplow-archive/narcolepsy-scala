



    // Create the Http Client and attach authentication
    val httpClient = new DefaultHttpClient
    httpClient.getCredentialsProvider().setCredentials(
      new AuthScope(request.getURI.getHost, request.getURI.getPort),
      new UsernamePasswordCredentials(apiKey, "")
    )