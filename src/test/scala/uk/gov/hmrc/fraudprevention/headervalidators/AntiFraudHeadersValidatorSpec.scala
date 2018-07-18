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

import play.api.test.FakeRequest
import uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidator._
import uk.gov.hmrc.fraudprevention.headervalidators.impl.GovClientPublicPortHeaderValidator
import uk.gov.hmrc.play.test.UnitSpec

class AntiFraudHeadersValidatorSpec extends UnitSpec {

  "AntiFraudHeadersValidator" should {

    "fail to build the header validators in case of unexpected header names" in {
      val ex = intercept[IllegalArgumentException] {
        val requiredHeaders = List("Fake-Header-Name", "Cache-Control")
        buildRequiredHeaderValidators(requiredHeaders)
      }
      ex.getMessage shouldBe s"There are no implementations for these headers: Fake-Header-Name, Cache-Control"
    }

    "return the header names for requests not including the required headers" in {
      val requiredHeaders = List(GovClientPublicPortHeaderValidator.headerName)
      val headerValidators = buildRequiredHeaderValidators(requiredHeaders)
      missingOrInvalidHeaderValues(headerValidators)(FakeRequest()) shouldBe requiredHeaders
    }

    "return the header names for requests having required headers with unexpected values" in {
      val requiredHeaders = List(GovClientPublicPortHeaderValidator.headerName)
      val headerValidators = buildRequiredHeaderValidators(requiredHeaders)
      missingOrInvalidHeaderValues(headerValidators)(FakeRequest().withHeaders(GovClientPublicPortHeaderValidator.headerName -> "")) shouldBe requiredHeaders
    }

    "return an empty list for requests that include the required headers with valid values" in {
      val requiredHeaders = List(GovClientPublicPortHeaderValidator.headerName)
      val headerValidators = buildRequiredHeaderValidators(requiredHeaders)
      missingOrInvalidHeaderValues(headerValidators)(FakeRequest().withHeaders(GovClientPublicPortHeaderValidator.headerName -> "111")) shouldBe List()
    }

  }

}
