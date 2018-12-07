/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.fraudprevention.headervalidators

import akka.stream.Materializer
import play.api.http.Status.{NO_CONTENT, PRECONDITION_FAILED}
import play.api.libs.json.Json
import play.api.mvc.Results.NoContent
import play.api.mvc._
import play.api.test.FakeRequest
import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidatorActionFilter._
import uk.gov.hmrc.fraudprevention.headervalidators.impl._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class AntiFraudHeadersValidatorActionFilterSpec extends UnitSpec with WithFakeApplication {

  private implicit val materializer: Materializer = fakeApplication.materializer

  private val defaultHeaderValidators: List[HeaderValidator] = List(
    GovClientDeviceIdHeaderValidator, GovClientUserIdHeaderValidator, GovClientTimezoneHeaderValidator,
    GovClientLocalIpHeaderValidator, GovVendorVersionHeaderValidator, GovVendorLicenseIdHeaderValidator,
    GovClientConnectionMethodHeaderValidator
  )

  Map(
    "action filter with default headers" -> actionFilterWithDefaultHeaders,
    "action filter from header validators" -> actionFilterFromHeaderValidators(defaultHeaderValidators),
    "action filter from header names" -> actionFilterFromHeaderNames(defaultHeaderValidators.map(hv => hv.headerName))
  ) foreach { case(filterName, actionFilter) =>

    filterName should {
      "return a successful response if no validations fail" in {
        val action: Action[AnyContent] = actionFilter(NoContent)
        val request = FakeRequest().withHeaders(defaultHeaderValidators.map(hd => hd.headerName -> "value"):_*)

        val result: Result = await(action(request))

        status(result) shouldBe NO_CONTENT
      }

      "return an error response if any validations fail" in {
        val action: Action[AnyContent] = actionFilter(NoContent)
        val request = FakeRequest().withHeaders(defaultHeaderValidators.tail.map(hd => hd.headerName -> "value"):_*)

        val result: Result = await(action(request))

        status(result) shouldBe PRECONDITION_FAILED
        jsonBodyOf(result) shouldBe Json.obj("code" -> "MISSING_OR_INVALID_HEADERS", "message" -> "Header Gov-Client-Device-ID is missing")
      }
    }
  }
}
