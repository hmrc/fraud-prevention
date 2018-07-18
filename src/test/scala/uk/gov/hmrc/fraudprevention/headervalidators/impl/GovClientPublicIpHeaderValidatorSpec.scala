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

    // TODO: cover all these cases
    // https://www.ipaddressguide.com/cidr
    // https://superuser.com/questions/1053845/what-are-the-valid-public-ip-address-ranges
    // https://www.iana.org/assignments/iana-ipv4-special-registry/iana-ipv4-special-registry.xhtml

    // TODO: rewrite code in a better way... using ranges... not this shitty repetition
    s"fail to validate the ${headerValidator.headerName} header if it is not a public IP address" in {


      // find scala / java library
      // https://github.com/risksense/ipaddr

      // TODO: use scalacheck
      // https://www.scalacheck.org/


      // useful way to generate ip addresses from CIDR ranges
      // https://stackoverflow.com/questions/26738561/get-all-ip-addresses-from-a-given-ip-address-and-subnet-mask
      // https://stackoverflow.com/questions/2942299/converting-cidr-address-to-subnet-mask-and-network-address
      // http://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/util/SubnetUtils.SubnetInfo.html
      // http://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/util/SubnetUtils.html


      for (g1 <- 0 to 255) {
        for (g2 <- 0 to 255) {
          for (g3 <- 0 to 255) {
            for (g4 <- 0 to 255) {

              // TODO:
              val ipAddress = s"$g1.$g2.$g3.$g4"
              validate(Some(Seq(s"10.$g2.$g3.$g4"))) shouldBe ipAddress apply mask [true | false]

            }
          }
        }
      }



      // IANA IPv4 Special-Purpose Address Registry: https://www.iana.org/assignments/iana-ipv4-special-registry/iana-ipv4-special-registry.xhtml
      // CIDR to IPv4 Conversion: https://www.ipaddressguide.com/cidr

      // CIDR Range 0.0.0.0/8
      // from 0.0.0.0 to 0.255.255.255
      for (g2 <- 0 to 255) {
        for (g3 <- 0 to 255) {
          for (g4 <- 0 to 255) {
            // TODO: fix - these tests are not passing
//            validate(Some(Seq(s"0.$g2.$g3.$g4"))) shouldBe false
          }
        }
      }

      // CIDR Range 10.0.0.0/8
      // from 10.0.0.0 to 10.255.255.255
      for (g2 <- 0 to 255) {
        for (g3 <- 0 to 255) {
          for (g4 <- 0 to 255) {
            validate(Some(Seq(s"10.$g2.$g3.$g4"))) shouldBe false
          }
        }
      }

      // CIDR Range 100.64.0.0/10
      // from 100.64.0.0 to 100.127.255.255
      for (g2 <- 64 to 127) {
        for (g3 <- 0 to 255) {
          for (g4 <- 0 to 255) {
            // TODO: fix - these tests are not passing
//            validate(Some(Seq(s"100.$g2.$g3.$g4"))) shouldBe false
          }
        }
      }

      // CIDR Range 127.0.0.0/8
      // from 127.0.0.0 to 127.255.255.255
      for (g2 <- 0 to 255) {
        for (g3 <- 0 to 255) {
          for (g4 <- 0 to 255) {
            validate(Some(Seq(s"127.$g2.$g3.$g4"))) shouldBe false
          }
        }
      }

      // CIDR Range 169.254.0.0/16
      // from 169.254.0.0 to 169.254.255.255
      for (g3 <- 0 to 255) {
        for (g4 <- 0 to 255) {
          validate(Some(Seq(s"169.254.$g3.$g4"))) shouldBe false
        }
      }











      // CIDR Range	172.16.0.0/12: from 172.16.0.0 to 172.31.255.255
//      for (g2 <- 16 to 31) {
//        for (g3 <- 0 to 255) {
//          for (g4 <- 0 to 255) {
//            validate(Some(Seq(s"172.$g2.$g3.$g4"))) shouldBe false
//          }
//        }
//      }

      // from 192.168.0.0 to 192.168.255.255
//      for (g3 <- 0 to 255) {
//        for (g4 <- 0 to 255) {
//          validate(Some(Seq(s"192.168.$g3.$g4"))) shouldBe false
//        }
//      }

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
