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

import play.api.http.ContentTypes._
import play.api.http.HeaderNames._
import play.api.test.FakeRequest
import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidator._
import uk.gov.hmrc.fraudprevention.headervalidators.impl._
import uk.gov.hmrc.play.test.UnitSpec

class AntiFraudHeadersValidatorSpec extends UnitSpec {

  private val headerValidators = List(GovClientPublicPortHeaderValidator, GovClientColourDepthHeaderValidator)

  "AntiFraudHeadersValidator.buildRequiredHeaderValidators()" should {

    "fail to build the header validators in case of unexpected header names" in {

      val ex = intercept[IllegalArgumentException] {
        val requiredHeaders = List("Fake-Header-Name", "Cache-Control")
        buildRequiredHeaderValidators(requiredHeaders)
      }

      ex.getMessage shouldBe "There are no implementations for these headers: Fake-Header-Name, Cache-Control"

    }

    "build the header validators from the header names" in {

      val requiredHeaders = List(GovClientPublicPortHeaderValidator.headerName,
        GovClientColourDepthHeaderValidator.headerName)

      buildRequiredHeaderValidators(requiredHeaders) shouldBe headerValidators

    }

  }

  "AntiFraudHeadersValidator.validate()" should {

    val extraHeaders: Seq[(String, String)] = Seq(
      USER_AGENT -> "Mozilla 4.2",
      CONTENT_TYPE -> JSON,
      ACCEPT -> JSON
    )

    "succeed to validate a request containing all the required headers with valid values" in {

      val request = FakeRequest().withHeaders(
        GovClientPublicPortHeaderValidator.headerName -> "12345",
        GovClientColourDepthHeaderValidator.headerName -> "24"
      ).withHeaders(extraHeaders:_*)

      validate(headerValidators)(request) shouldBe Right(())

    }

    "combine the validation error messages for all invalid headers" in {

      val request = FakeRequest().withHeaders(
        GovClientPublicPortHeaderValidator.headerName -> "-1",
        GovClientColourDepthHeaderValidator.headerName -> "-00"
      )

      val Left(errors) = validate(headerValidators)(request)
      errors shouldBe List("Invalid port number for header Gov-Client-Public-Port: -1",
        "Regular expression not matching for header Gov-Client-Colour-Depth: -00")

    }

    "fail to validate a request missing some of the required headers" in {

      val request = FakeRequest().withHeaders(
        GovClientPublicPortHeaderValidator.headerName -> "23"
      )

      validate(headerValidators)(request) shouldBe Left(List("Header Gov-Client-Colour-Depth is missing"))

    }

    "fail to validate a request containing multiple values for required headers that must have one value only" in {

      val request = FakeRequest().withHeaders(
        GovClientPublicPortHeaderValidator.headerName -> "123",
        GovClientPublicPortHeaderValidator.headerName -> "456"
      ).withHeaders(
        GovClientColourDepthHeaderValidator.headerName -> "12"
      )

      validate(headerValidators)(request) shouldBe Left(List("Multiple values for header Gov-Client-Public-Port"))

    }

  }

}
