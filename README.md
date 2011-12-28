Narcolepsy - A declarative framework for building RESTful API clients in Scala.

narcolepsy-scala is a Scala framework to help you work quickly and safely with third-party RESTful web services. Narcolepsy is modelled closely after Squeryl: it provides a typesafe, ORM-like abstraction layer on top of HTTP method-based, resource-oriented web services. As such it works best with APIs which conform closely to the RESTful ideal.

# Health warning

Narcolepsy is highly incomplete. Error handling, POST, PUT and DELETE are all still to come. Sorry about this - I just wanted to release early (not least because SBT doesn't support private GitHub repositories as dependencies). If there is a specific feature you would like to see sooner rather than later, please create a ticket.

# How Narcolepsy works

Narcolepsy is designed as an extensible toolkit which you implement and configure on a per-web service basis. An example of a Narcolepsy-based web service client is Prestasac, the PrestaShop Scala client. Narcolepsy has three main modes of operation:

* Explore - for when you're starting out and are not yet clear on the exact nature of the target web service's API. Use Explore mode to test out GETs, POSTs etc. This mode is as close as I could make it to the excellent resty command-line tool - but of course it works within the Scala REPL. This mode is coming very soon
* Marshall - mode coming soon
* Declare - for when you have a full understanding of the target web service, and are ready to formalise your interactions by defining JAXB (XML) and/or Jackson (JSON) bindings for all of the most important representations. This mode is the most time-consuming to setup, but once it's ready, users of your client library can interact with the web service as a set of typesafe objects, while still having direct access to the HTTP verbs

# Who shouldn't use Narcolepsy

Narcolepsy probably isn't for you if one of the following things is true:

* The target web service is fragile or frequently changing
* You only need to extract a small subset of data from the target web service
* Your target web service is non-RESTful - in particular, if it is imperative/RPC/SOAP-like 

An alternative to Narcolepsy for these use cases is the excellent 

# Technologies


