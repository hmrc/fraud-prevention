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

package uk.gov.hmrc.fraudprevention.headervalidators

import java.net.{Inet4Address, InetAddress}

import play.api.mvc.RequestHeader

import scala.util.{Success, Try}

trait IpAddressHeaderValidator extends HeaderValidator {

  private def toV4IpAddress: String => Inet4Address = {
    InetAddress.getByName(_).asInstanceOf[Inet4Address]
  }

  protected def isPublicIpAddress: Inet4Address => Boolean = { i: Inet4Address =>
    !i.isSiteLocalAddress &&
    !i.isAnyLocalAddress  &&
    !i.isLinkLocalAddress &&
    !i.isLoopbackAddress  &&
    !i.isMulticastAddress
  }

  protected def isPrivateIpAddress: Inet4Address => Boolean = {
    _.isSiteLocalAddress
  }

  protected def v4IpAddresses(request: RequestHeader): Try[Seq[Inet4Address]] = {
    Try ( requestHeaderValues(request, headerName).map(toV4IpAddress) )
  }

  override final def isValidHeader(request: RequestHeader): Boolean = {
    hasOneHeaderValueOnly(request) && validV4IpAddresses(request)
  }

  protected def isAllowedIp: Inet4Address => Boolean

  private def validV4IpAddresses(request: RequestHeader): Boolean = {
    v4IpAddresses(request) match {
      case Success(ips: Seq[Inet4Address]) => ips.forall(isAllowedIp)
      case _ => false
    }
  }

}
