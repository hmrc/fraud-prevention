
# Fraud Prevention

 [ ![Download](https://api.bintray.com/packages/hmrc/releases/fraud-prevention/images/download.svg) ](https://bintray.com/hmrc/releases/fraud-prevention/_latestVersion)

This Play library helps API producers to validate headers on incoming requests to their APIs.
More information can be found [here](https://developer.service.hmrc.gov.uk/api-documentation/docs/reference-guide#fraud-prevention).

## Installing

Include the following dependency in your SBT build

``` scala
resolvers += Resolver.bintrayRepo("hmrc", "releases")

libraryDependencies += "uk.gov.hmrc" %% "fraud-prevention" % "[INSERT-VERSION]"
```

Note that the library is only available for Play 2.5.x.

## Usage

The library can be used for rejecting requests that have missing headers or have headers with invalid values.
We made available a Scala object called `AntiFraudHeadersValidator` and a Play `ActionFilter` called `AntiFraudHeadersValidatorActionFilter`.
Your API controllers can use either of these two approaches.

If you use `AntiFraudHeadersValidator`, you first need to initialise the header validators:
``` scala

import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidator

val requiredHeaders = List("Gov-Client-Public-Port")
val headerValidators = AntiFraudHeadersValidator.buildRequiredHeaderValidators(requiredHeaders)
```

Then, for each incoming request to the API, your controllers have to execute the following code:
``` scala

import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidator

AntiFraudHeadersValidator.missingOrInvalidHeaderValues(headerValidators) match {
  case None => // you should continue processing the request
  case Some(missingOrInvalidHeaders: List[String]) => //  you should block the request (because of the missing or invalid headers)
}

```

If you use the `AntiFraudHeadersValidatorActionFilter`, your controller would look simpler.
We suggest two alternative implementations.

1. By creating the Play `ActionFilter` from the required header names:
``` scala

import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidator

lazy val requiredHeaders = List("Gov-Client-Public-Port")

// this can be reused for each controller that require the same headers
lazy val fraudPreventionActionFilter = AntiFraudHeadersValidatorActionFilter.actionFilterFromHeaderNames(requiredHeaders)

def handleRequest(): Action[AnyContent] = fraudPreventionActionFilter.async { implicit request =>
  // controller code to add
}
```

2. By creating the Play `ActionFilter` from the header validators:
``` scala

import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidator

lazy val requiredHeaders = List("Gov-Client-Public-Port")
lazy val headerValidators = AntiFraudHeadersValidator.buildRequiredHeaderValidators(requiredHeaders)

// this can be reused for each controller that require the same headers
lazy val fraudPreventionActionFilter = AntiFraudHeadersValidatorActionFilter.actionFilterFromHeaderValidators(headerValidators)

def handleRequest(): Action[AnyContent] = fraudPreventionActionFilter.async { implicit request =>
  // controller code to add
}
```

---

## Unit tests
```
sbt test
```

---

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
