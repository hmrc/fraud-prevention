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

import java.net.Inet4Address

import uk.gov.hmrc.fraudprevention.headervalidators.IpAddressHeaderValidator

object GovClientLocalIpHeaderValidator extends IpAddressHeaderValidator {

  override val headerName: String = "Gov-Client-Local-IP"

  override protected def isAllowedIp: Inet4Address => Boolean = {
    isPrivateIpAddress
  }

}