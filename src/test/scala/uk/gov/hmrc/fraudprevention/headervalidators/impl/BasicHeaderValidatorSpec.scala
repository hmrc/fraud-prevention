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

package uk.gov.hmrc.fraudprevention.headervalidators.impl

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator
import uk.gov.hmrc.play.test.UnitSpec

class BasicHeaderValidatorSpec extends HeaderValidatorBaseSpec with UnitSpec {

  val basicHeaderValidators = List(
    GovClientDeviceIdHeaderValidator, GovClientUserIdHeaderValidator, GovClientTimezoneHeaderValidator,
    GovClientLocalIpHeaderValidator, GovVendorVersionHeaderValidator, GovVendorLicenseIdHeaderValidator,
    GovClientConnectionMethodHeaderValidator
  )

  basicHeaderValidators foreach { headerValidator: HeaderValidator =>
    headerValidator.getClass.getSimpleName should {

      "return a valid result if the value is valid" in {
        val headerValues = Some(Seq("test"))
        val result: ValidatedNel[String, Unit] = validateRequest(headerValidator, headerValues)
        result shouldBe Valid(())
      }

      s"fail to validate the ${headerValidator.headerName} header if there is no value" in {
        val headerValues = None
        val Invalid(nel) = validateRequest(headerValidator, headerValues)
        nel.toList shouldBe List(s"")
      }

      s"fail to validate the ${headerValidator.headerName} header if it is the empty string" in {
        val headerValues = Some(Seq(""))
        val Invalid(nel) = validateRequest(headerValidator, headerValues)
        nel.toList shouldBe List(s"${headerValidator.headerName} must not be empty")
      }

      s"fail to validate the ${headerValidator.headerName} header if there are multiple values" in {
        val headerValues = Some(Seq("12", "24"))
        val Invalid(nel) = validateRequest(headerValidator, headerValues)
        nel.toList shouldBe List(s"Multiple values for header ${headerValidator.headerName}")
      }
    }
  }
}
