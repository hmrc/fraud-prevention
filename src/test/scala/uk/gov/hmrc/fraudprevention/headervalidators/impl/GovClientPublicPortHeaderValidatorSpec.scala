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

import uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator
import uk.gov.hmrc.play.test.UnitSpec

class GovClientPublicPortHeaderValidatorSpec extends UnitSpec with HeaderValidatorBaseSpec {

  "GovClientPublicPortHeaderValidator" should {

    s"fail to validate the ${headerValidator.headerName} header if there is no value" in {
      validate(None) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if it is the empty string" in {
      validate(Some(Seq(""))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if it does not contain a number" in {
      validate(Some(Seq("a"))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if it is a negative number" in {
      validate(Some(Seq("-3"))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if it is zero" in {
      validate(Some(Seq("0"))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if it bigger than 65535" in {
      validate(Some(Seq("65536"))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if it is a decimal number" in {
      validate(Some(Seq("0.165"))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if there are multiple values" in {
      validate(Some(Seq("2333", "4445"))) shouldBe false
    }

    s"validate the ${headerValidator.headerName} header if it is an allowed port number" in {
      for (p <- 1 to 65535) {
        validate(Some(Seq(p.toString))) shouldBe true
      }
    }

  }

  override protected def headerValidator: HeaderValidator = GovClientPublicPortHeaderValidator

}
