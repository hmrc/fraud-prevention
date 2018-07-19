
# Fraud Prevention

 [ ![Download](https://api.bintray.com/packages/hmrc/releases/fraud-prevention/images/download.svg) ](https://bintray.com/hmrc/releases/fraud-prevention/_latestVersion)

This library supports API producers to validate request headers on API incoming requests.
More information can be found [here](https://developer.service.hmrc.gov.uk/api-documentation/docs/reference-guide#fraud-prevention).

## Installing

Include the following dependency in your SBT build

``` scala
resolvers += Resolver.bintrayRepo("hmrc", "releases")

libraryDependencies += "uk.gov.hmrc" %% "fraud-prevention" % "[INSERT-VERSION]"
```

Note that the library is only available for Play 2.5.x.

## Usage

The library can be used for rejecting requests that have missing headers or with invalid values.
We made available a Play `ActionFilter` called `AntiFraudHeadersValidatorActionFilter` and a Scala object that can be used.
Your API controllers could either use the action filter or the


controllers could either use



in the Play controllers that are being called by the API resources that need to validate the required headers.



You can either




First identify what headers are required for your API.

```scala


import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters.formatNoFraudAnswer
import uk.gov.hmrc.apiplatformtest.models.NoFraudAnswer
import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidatorActionFilter

import scala.concurrent.Future.successful

trait MyController {

  lazy private val requiredHeaders = List("Gov-Client-Public-Port")

  def handle(): Action[AnyContent] = {
   (Action andThen AntiFraudHeadersValidatorActionFilter.actionFilterFromHeaderNames(requiredHeaders)).async { implicit request =>
      successful(
        Ok(Json.toJson(NoFraudAnswer("All required headers have been sent correctly in the request.")))
      )
    }
  }

}

object MyController extends MyController





```

---

## Unit tests
```
sbt test
```

---

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
