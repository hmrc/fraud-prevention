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

package uk.gov.hmrc.fraudprevention

import play.api.mvc._
import uk.gov.hmrc.fraudprevention.headervalidators.{HeaderValidator, HeaderValidatorUtils}
import uk.gov.hmrc.fraudprevention.headervalidators.impl._
import uk.gov.hmrc.fraudprevention.model.{ErrorConversion, ErrorResponse}

import scala.concurrent.Future

object AntiFraudHeadersValidator extends HeaderValidatorUtils {

  private lazy val requiredHeaderValidators: List[HeaderValidator] = List(
    GovClientPublicPortHeaderValidator
  )

  private lazy val requiredHeaderNames: List[String] = requiredHeaderValidators.map(_.headerName)

  // to be called only once, at start-up of the API microservice
  def buildRequiredHeaderValidators(requiredHeaders: List[String]): List[HeaderValidator] = {

    val (validHeaderNames, invalidHeaderNames) = requiredHeaders.partition ( requiredHeaderNames.contains )

    if (invalidHeaderNames.nonEmpty) {
      throw new IllegalArgumentException(s"There are no implementations for these headers: ${invalidHeaderNames.mkString(", ")}")
    }

    requiredHeaderValidators.filter ( h => validHeaderNames.contains ( h.headerName ) )
  }

  // to be called for each API incoming request
  def missingOrInvalidHeaderValues(requiredHeaders: List[HeaderValidator])(request: RequestHeader): List[String] = {
    requiredHeaders.filterNot ( _.isValidHeader(request) ).map( _.headerName )
  }

}

object AntiFraudHeadersValidatorActionFilter extends ErrorConversion {

  def actionFilterFromHeaderNames(requiredHeaders: List[String]) = {
    val requiredHeaderValidators = AntiFraudHeadersValidator.buildRequiredHeaderValidators(requiredHeaders)
    actionFilterFromHeaderValidators(requiredHeaderValidators)
  }

  def actionFilterFromHeaderValidators(requiredHeaderValidators: List[HeaderValidator]) = new ActionBuilder[Request] with ActionFilter[Request] {

    override protected def filter[A](request: Request[A]): Future[Option[Result]] = Future.successful {

      implicit val r: Request[A] = request

      val missingOrInvalidHeaderValues = AntiFraudHeadersValidator.missingOrInvalidHeaderValues(requiredHeaderValidators)(request) match {
        case Nil => None
        case errors: List[String] => Some(s"Missing or invalid headers: ${errors.mkString(", ")}")
      }

      missingOrInvalidHeaderValues.map(ErrorResponse.buildErrorMessage)
    }

  }

}
