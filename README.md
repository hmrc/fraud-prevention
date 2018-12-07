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

If you use `AntiFraudHeadersValidator`, you first need the header validators.
As an example, assuming that you want to validate the _Gov-Client-Public-Port_ header only, this is the code you need:
``` scala
import uk.gov.hmrc.fraudprevention.headervalidators.impl.GovClientPublicPortHeaderValidator

val headerValidators = List(GovClientPublicPortHeaderValidator)
```

You can also initialise the validators from the header names:
``` scala
import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidator

val requiredHeaders = List("Gov-Client-Public-Port")
val headerValidators = AntiFraudHeadersValidator.buildRequiredHeaderValidators(requiredHeaders)
```

Then, once you have the validators for all your required headers, for each incoming request to the API, your controllers have to execute the following code:
``` scala
import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidator

AntiFraudHeadersValidator.validate(headerValidators)(request) match {
  case Right(_) => // you should continue processing the request
  case Left(errors: List[String]) => // you should block the request (because of the missing or invalid headers)
}
```

If you use the `AntiFraudHeadersValidatorActionFilter`, your controller would look simpler.
You just have to wrap your code with the filter, which will return a precondition failed (412) error if the validation of the headers fails.
We suggest three alternative implementations.

1. By creating the Play `ActionFilter` using the default headers:
``` scala
import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidatorActionFilter

// this can be reused for each controller that require the same headers
lazy val fraudPreventionActionFilter = AntiFraudHeadersValidatorActionFilter.actionFilterWithDefaultHeaders

def handleRequest(): Action[AnyContent] = fraudPreventionActionFilter.async { implicit request =>
  // controller code to add
}
```

The default headers are:
- Gov-Client-Device-ID
- Gov-Client-User-IDs
- Gov-Client-Timezone
- Gov-Client-Local-IPs
- Gov-Vendor-Version
- Gov-Vendor-License-IDs
- Gov-Client-Connection-Method

2. By creating the Play `ActionFilter` from the required header names:
``` scala
import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidatorActionFilter

lazy val requiredHeaders = List("Gov-Client-Public-Port")

// this can be reused for each controller that require the same headers
lazy val fraudPreventionActionFilter = AntiFraudHeadersValidatorActionFilter.actionFilterFromHeaderNames(requiredHeaders)

def handleRequest(): Action[AnyContent] = fraudPreventionActionFilter.async { implicit request =>
  // controller code to add
}
```

3. By creating the Play `ActionFilter` from the header validators:
``` scala
import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidatorActionFilter
import uk.gov.hmrc.fraudprevention.headervalidators.impl.GovClientPublicPortHeaderValidator

lazy val headerValidators = List(GovClientPublicPortHeaderValidator)

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
