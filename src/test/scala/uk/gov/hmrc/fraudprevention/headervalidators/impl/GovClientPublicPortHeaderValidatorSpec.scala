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

import uk.gov.hmrc.play.test.UnitSpec

class GovClientPublicPortHeaderValidatorSpec extends UnitSpec {

  "GovClientPublicPortHeaderValidator" should {

    s"fail to validate the $headerName header if there is no value" in {
      validatePortNumbers(None) shouldBe false
    }

    s"fail to validate the $headerName header if it is the empty string" in {
      validatePortNumbers(Some(Seq(""))) shouldBe false
    }

    s"fail to validate the $headerName header if it does not contain a number" in {
      validatePortNumbers(Some(Seq("a"))) shouldBe false
    }

    s"fail to validate the $headerName header if it is a negative number" in {
      validatePortNumbers(Some(Seq("-3"))) shouldBe false
    }

    s"fail to validate the $headerName header if it is zero" in {
      validatePortNumbers(Some(Seq("0"))) shouldBe false
    }

    s"fail to validate the $headerName header if it bigger than 65535" in {
      validatePortNumbers(Some(Seq("65536"))) shouldBe false
    }

    s"fail to validate the $headerName header if it is a decimal number" in {
      validatePortNumbers(Some(Seq("0.165"))) shouldBe false
    }

    s"validate the $headerName header if it is a valid port number" in {
      validatePortNumbers(Some(Seq("1", "234", "11111", "65535"))) shouldBe true
    }

  }

  private def headerName: String = GovClientPublicPortHeaderValidator.headerName

  private def validatePortNumbers(portNumbers: Option[Seq[String]]): Boolean = {
    GovClientPublicPortHeaderValidator.isValidHeader(portNumbers)
  }

}
