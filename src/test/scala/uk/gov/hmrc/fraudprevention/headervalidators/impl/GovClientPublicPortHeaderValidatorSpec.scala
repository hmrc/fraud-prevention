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

class GovClientPublicPortHeaderValidatorSpec extends HeaderValidatorBaseSpec with UnitSpec {

  val headerValidator: HeaderValidator = GovClientPublicPortHeaderValidator

  "GovClientPublicPortHeaderValidator" should {

    s"fail to validate the ${headerValidator.headerName} header if there is no value" in {
      val headerValues = None
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"")
    }

    s"fail to validate the ${headerValidator.headerName} header if it is the empty string" in {
      val port = ""
      val headerValues = Some(Seq(port))
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"Invalid port number for header ${headerValidator.headerName}: $port")
    }

    s"fail to validate the ${headerValidator.headerName} header if it does not contain a number" in {
      val port = "a"
      val headerValues = Some(Seq(port))
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"Invalid port number for header ${headerValidator.headerName}: $port")
    }

    s"fail to validate the ${headerValidator.headerName} header if it is a negative number" in {
      val port = -4
      val headerValues = Some(Seq(port.toString))
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"Invalid port number for header ${headerValidator.headerName}: $port")
    }

    s"fail to validate the ${headerValidator.headerName} header if it is zero" in {
      val port = 0
      val headerValues = Some(Seq(port.toString))
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"Invalid port number for header ${headerValidator.headerName}: $port")
    }

    s"fail to validate the ${headerValidator.headerName} header if it bigger than 65535" in {
      val port = 65536
      val headerValues = Some(Seq(port.toString))
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"Invalid port number for header ${headerValidator.headerName}: $port")
    }

    s"fail to validate the ${headerValidator.headerName} header if it is a decimal number" in {
      val port = 0.165
      val headerValues = Some(Seq(port.toString))
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"Invalid port number for header ${headerValidator.headerName}: $port")
    }

    s"fail to validate the ${headerValidator.headerName} header if there are multiple values" in {
      val headerValues = Some(Seq("2333", "4445"))
      val Invalid(nel) = validateRequest(headerValidator, headerValues)
      nel.toList shouldBe List(s"Multiple values for header ${headerValidator.headerName}")
    }

    s"validate the ${headerValidator.headerName} header if it is an allowed port number" in {
      for (p <- 1 to 65535) {
        val headerValues = Some(Seq(s"$p"))
        validateRequest(headerValidator, headerValues) shouldBe ().validNel
      }
    }

  }

}
