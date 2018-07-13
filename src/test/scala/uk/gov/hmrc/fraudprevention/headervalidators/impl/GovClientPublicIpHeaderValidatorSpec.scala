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

class GovClientPublicIpHeaderValidatorSpec extends UnitSpec {

  "GovClientPublicIpHeaderValidator" should {

    s"fail to validate the $headerName header if there is no value" in {
      validateIpAddresses(None) shouldBe false
    }

    s"fail to validate the $headerName header if it is the empty string" in {
      validateIpAddresses(Some(Seq(""))) shouldBe false
    }

    s"fail to validate the $headerName header if it does not contain 4 groups" in {
      validateIpAddresses(Some(Seq("192.168.1.1.2.3"))) shouldBe false
    }

    s"fail to validate the $headerName header if a group does not contain digits" in {
      validateIpAddresses(Some(Seq("192.168.1.1w"))) shouldBe false
    }

    s"fail to validate the $headerName header if a group contains a number > 255" in {
      validateIpAddresses(Some(Seq("192.168.1.256"))) shouldBe false
    }

    s"fail to validate the $headerName header if a group contains an IP address in IPv6 format" in {
      validateIpAddresses(Some(Seq("001:db8:0:1234:0:567:8:1"))) shouldBe false
    }

    s"fail to validate the $headerName header if one of the header values is invalid" in {
      validateIpAddresses(Some(Seq("192.168.1.156", "1.1.1.356", "8.8.8.8"))) shouldBe false
    }

    s"validate the $headerName header if all the values are valid IP addresses using the IPv4 format" in {
      validateIpAddresses(Some(Seq("192.168.1.255", "1.1.1.1", "8.8.8.8"))) shouldBe true
    }

  }

  private def headerName: String = GovClientPublicIpHeaderValidator.headerName

  private def validateIpAddresses(ipAddresses: Option[Seq[String]]): Boolean = {
    GovClientPublicIpHeaderValidator.isValidHeader(ipAddresses)
  }

}
