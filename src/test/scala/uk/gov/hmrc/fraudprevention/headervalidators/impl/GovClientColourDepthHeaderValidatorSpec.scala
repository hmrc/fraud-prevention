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

import cats.data.Validated.Invalid
import cats.implicits._
import uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator
import uk.gov.hmrc.play.test.UnitSpec

class GovClientColourDepthHeaderValidatorSpec extends HeaderValidatorBaseSpec with UnitSpec {

  val headerValidator: HeaderValidator = GovClientColourDepthHeaderValidator

  "GovClientColourDepthHeaderValidator" should {

    s"fail to validate the ${headerValidator.headerName} header if there is no value" in {
      val headerValues = None
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"Header ${headerValidator.headerName} is missing")
    }

    s"fail to validate the ${headerValidator.headerName} header if it is the empty string" in {
      val value = ""
      val headerValues = Some(Seq(value))
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"Regular expression not matching for header ${headerValidator.headerName}: $value")
    }

    s"fail to validate the ${headerValidator.headerName} header if it does not contain a number" in {
      val value = "a"
      val headerValues = Some(Seq(value))
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"Regular expression not matching for header ${headerValidator.headerName}: $value")
    }

    s"fail to validate the ${headerValidator.headerName} header if it is a negative number" in {
      val value = -4
      val headerValues = Some(Seq(value.toString))
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"Regular expression not matching for header ${headerValidator.headerName}: $value")
    }

    s"fail to validate the ${headerValidator.headerName} header if it is a decimal number" in {
      val value = 34.165
      val headerValues = Some(Seq(value.toString))
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"Regular expression not matching for header ${headerValidator.headerName}: $value")
    }

    s"fail to validate the ${headerValidator.headerName} header if there are multiple values" in {
      val headerValues = Some(Seq("12", "24"))
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"Multiple values for header ${headerValidator.headerName}")
    }

    s"validate the ${headerValidator.headerName} header if it is an allowed colour depth value" in {
      for (d1 <- 0 to 9) {
        for (d2 <- 0 to 9) {
          for (d3 <- 0 to 9) {
            val headerValues = Some(Seq(s"$d1$d2$d3"))
            validateRequest(headerValidator, headerValues) shouldBe ().validNel
          }
        }
      }
    }

  }

}
