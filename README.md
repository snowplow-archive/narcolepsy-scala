# Narcolepsy - A declarative framework for building RESTful API clients in Scala

Narcolepsy is a Scala framework to help you work efficiently and safely with third-party RESTful web services. Narcolepsy is modelled closely after [Squeryl](http://squeryl.org/): it provides a typesafe, ORM-like abstraction layer on top of HTTP method-based, resource-oriented web services. As such it works best with APIs which conform closely to the RESTful ideal.

# Health warning

Narcolepsy is highly incomplete. Error handling, POST and PUT are all still to come. Apologies - we wanted to release early (not least because SBT doesn't support private GitHub repositories as dependencies). If there is a specific feature you would like to see sooner rather than later, please create a [GitHub issue](https://github.com/orderly/narcolepsy-scala/issues) for it.

# How Narcolepsy works

Narcolepsy is designed as an extensible toolkit which you implement and configure on a per-web service basis. An example of a Narcolepsy-based web service client is [Prestasac](https://github.com/orderly/codeigniter-paypal-ipn), a Scala client for PrestaShop. Narcolepsy has three main modes of operation:

* **Explore** - for when you're starting out and are not yet clear on the exact nature of the target web service's API. Use Explore mode to test out GETs, POSTs etc. This mode is modelled after the excellent [resty](https://github.com/micha/resty) command-line tool - but of course it works within the Scala REPL. This mode is coming very soon
* **Agile** - mode coming soon
* **Declare** - for when you have a full understanding of the target web service, and are ready to formalise your interactions by defining JAXB (XML) and/or Jackson (JSON) bindings for all of the most important representations. This mode is the most time-consuming to setup, but once it's ready, users of your client library can interact with the web service as a set of typesafe objects, while still having direct access to the HTTP verbs

# Who shouldn't use Narcolepsy

Narcolepsy probably isn't for you if any of the following things is true:

* The target web service is fragile or frequently changing
* You only need to extract a small subset of data from the target web service
* Your target web service is non-RESTful - in particular, if it is RPC/SOAP-like 

In these cases, we strongly recommend using the excellent [Unfiltered](http://unfiltered.databinder.net/Unfiltered.html) Scala toolkit instead of Narcolepsy. 

# Technologies

Narcolepsy uses [Jackson](http://jackson.codehaus.org/) for JSON marshalling and unmarshalling, and JAXB for XML. In theory Narcolepsy can work with any JAXB implementation - it has been tested with the Oracle Glassfish [JAXB reference implementation](http://jaxb.java.net/), and also with the Eclipse Foundation's [EclipseLink MOXy](http://eclipse.org/eclipselink/moxy.php). Some of Narcolepsy's functionality (for example supporting underscored or hyphenated element names) are only compatible with MOXy.

By default Narcolepsy uses the Apache HttpComponents [HttpClient](http://hc.apache.org/httpcomponents-client-ga/) for HTTP communication with the target web service. However, HttpClient can be swapped out for an alternative 'adapter' - none have been written yet but we plan to build one for [spray-client](https://github.com/spray/spray/wiki/spray-client). 

# License

Like its main component technologies, Narcolepsy is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html). 
