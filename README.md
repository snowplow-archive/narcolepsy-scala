# Narcolepsy - A Scala framework for building typesafe API clients

Narcolepsy is a Scala framework to help you build clients for third-party RESTful web services with maximum typesafety and minimal moving parts. Narcolepsy is modelled closely after [Squeryl](http://squeryl.org/): it provides a typesafe, ORM-like abstraction layer on top of HTTP method-based, resource-oriented web services. As such it works best with APIs which conform closely to the [RESTful ideal](http://blog.steveklabnik.com/posts/2011-07-03-nobody-understands-rest-or-http).

# Health warning

Narcolepsy is highly incomplete. Error handling, non-HTTP-based authentication, POST, PUT and HEAD support are all still to come. Apologies - we wanted to release early (not least because SBT doesn't support private GitHub repositories as dependencies). If there is a specific feature you would like to see sooner rather than later, please create a [GitHub issue](https://github.com/orderly/narcolepsy-scala/issues) for it.

# How Narcolepsy works

Narcolepsy is designed as an extensible toolkit which you implement and configure on a per-web service basis. An example of a Narcolepsy-based web service client is [Prestasac](https://github.com/orderly/codeigniter-paypal-ipn), a Scala client for PrestaShop. Narcolepsy supports two distinct modes of operation:

* **Raw** - use **Raw** mode to test out different interactions with the target web service. This mode is similar to the excellent [resty](https://github.com/micha/resty) command-line tool. Unlike **Typesafe** mode below, **Raw** mode is available "out of the box" with Narcolepsy and should work with most RESTful web services with zero additional coding
* **Typesafe** - this mode is for when you have a full understanding of the target representations, and are ready to formalise your interactions by defining typesafe bindings for these representations. To enable this mode, you will need to setup class definitions for all of the representations that you care out. Once you have done this, users of your Narcolepsy-based client library can interact with the web service's representations as a set of typesafe objects

Currently Narcolepsy works with XML and JSON-based web services only. We plan to support [JSON Schema](http://json-schema.org/) and [Apache Avro](http://avro.apache.org/docs/1.4.0/index.html) in due course.

# Who shouldn't use Narcolepsy

Narcolepsy probably isn't for you if any of the following things is true:

* Your target web service is fragile or frequently changing
* You only need to extract a small subset of data from your target web service
* Your target web service is non-RESTful - for example, it is RPC/SOAP-like

In these cases, we strongly recommend using the excellent [Unfiltered](http://unfiltered.databinder.net/Unfiltered.html) Scala toolkit instead of Narcolepsy. 

# Technologies

Narcolepsy uses [Jackson](http://jackson.codehaus.org/) for JSON (un)marshalling, and JAXB for XML (un)marshalling. In theory Narcolepsy can work with any JAXB implementation - it has been tested with the Oracle Glassfish [JAXB reference implementation](http://jaxb.java.net/), and also with the Eclipse Foundation's [EclipseLink MOXy](http://eclipse.org/eclipselink/moxy.php). A small subset of Narcolepsy's XML functionality is only compatible with MOXy; MOXy-only functionality is clearly marked in the [moxy sub-package](https://github.com/orderly/narcolepsy-scala/tree/master/src/main/scala/co/orderly/narcolepsy/marshallers/xml/moxy).

Although we write our binding classes for **Typesafe** mode in Scala, Jackson and JAXB are pure-Java technologies, and so writing your bindings in Java should work fine as well.

By default Narcolepsy uses the Apache HttpComponents [HttpClient](http://hc.apache.org/httpcomponents-client-ga/) for HTTP communication with the target web service. However, HttpClient can be swapped out for an alternative 'adapter' - none have been written yet but we plan to build one for [spray-client](https://github.com/spray/spray/wiki/spray-client).

# License

Like its main component technologies, Narcolepsy is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html). 
