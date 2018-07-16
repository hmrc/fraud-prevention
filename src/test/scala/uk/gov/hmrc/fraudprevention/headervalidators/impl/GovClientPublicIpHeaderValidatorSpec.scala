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

class GovClientPublicIpHeaderValidatorSpec extends UnitSpec with HeaderValidatorBaseSpec {

  // TODO: separate scenarios in blocks
  // TODO: tests for each (test all possible ip address that are not public!!!)

  "IpAddressHeaderValidator" should {

    s"fail to validate the ${headerValidator.headerName} header if there is no value" in {
      validate(None) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if it is the empty string" in {
      validate(Some(Seq(""))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if it does not contain 4 groups" in {
      validate(Some(Seq("192.168.1.1.2.3"))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if a group does not contain digits" in {
      validate(Some(Seq("192.168.1.www"))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if a group contains a number > 255" in {
      validate(Some(Seq("192.168.1.256"))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if a group contains an IP address in IPv6 format" in {
      validate(Some(Seq("001:db8:0:1234:0:567:8:1"))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if it is a private IP address" in {
      validate(Some(Seq("10.1.2.3"))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if it is localhost" in {
      validate(Some(Seq("127.0.0.1"))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if it is link local" in {
      validate(Some(Seq("169.254.0.0"))) shouldBe false
    }

    s"fail to validate the ${headerValidator.headerName} header if the header contains multiple values" in {
      validate(Some(Seq("192.168.1.156", "8.8.8.8"))) shouldBe false
    }

    s"validate the ${headerValidator.headerName} header if there is only one value using the IPv4 format" in {
      validate(Some(Seq("191.168.1.254"))) shouldBe true
    }

    // TODO: validate better all scenarios
    //  https://www.iana.org/assignments/iana-ipv4-special-registry/iana-ipv4-special-registry.xhtml
  }

  override protected def headerValidator: HeaderValidator = GovClientPublicIpHeaderValidator

}
