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

package uk.gov.hmrc.fraudprevention.model

import uk.gov.hmrc.play.test.UnitSpec

class ErrorResponseSpec extends UnitSpec {

  "ErrorResponse" should {

    "build the correct error response from a String" in {
      val msg = "Something is broken"
      ErrorResponse(List(msg)) shouldBe ErrorResponse("MISSING_OR_INVALID_HEADERS", msg)
    }

    "build the correct error response from a List" in {
      val msg = List("error 1", "error 2", "error 3")
      ErrorResponse(msg) shouldBe ErrorResponse("MISSING_OR_INVALID_HEADERS", "error 1, error 2, error 3")
    }

  }

}
