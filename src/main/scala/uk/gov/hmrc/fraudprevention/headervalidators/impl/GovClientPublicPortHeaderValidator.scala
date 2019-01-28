/*
 * Copyright 2019 HM Revenue & Customs
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

import uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator

import scala.util.{Success, Try}

object GovClientPublicPortHeaderValidator extends HeaderValidator {

  override final val headerName: String = "Gov-Client-Public-Port"

  private val MINIMUM_ALLOWED_PORT_NUMBER = 1
  private val MAXIMUM_ALLOWED_PORT_NUMBER = 65535

  private def isAllowedNumber: Int => Boolean = { p: Int =>
    p >= MINIMUM_ALLOWED_PORT_NUMBER && p <= MAXIMUM_ALLOWED_PORT_NUMBER
  }

  override protected def validateHeaderValue(headerValue: String): Either[String, Unit] = {
    Try ( headerValue.toInt ) match {
      case Success(p: Int) if isAllowedNumber(p) => Right(())
      case _ => Left(s"Invalid port number for header $headerName: $headerValue")
    }
  }

}

