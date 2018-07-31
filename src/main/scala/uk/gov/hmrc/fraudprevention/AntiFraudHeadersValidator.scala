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

import cats.implicits._
import play.api.mvc._
import uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator
import uk.gov.hmrc.fraudprevention.headervalidators.impl._
import uk.gov.hmrc.fraudprevention.model.HeadersValidation.HeadersValidation
import uk.gov.hmrc.fraudprevention.model.{ErrorConversion, ErrorResponse}

import scala.concurrent.Future

object AntiFraudHeadersValidator {

  private lazy val headerValidators: List[HeaderValidator] = List(
    GovClientPublicPortHeaderValidator,
    GovClientColourDepthHeaderValidator
  )

  private lazy val headerNames: List[String] = headerValidators.map(_.headerName)

  /**
    * Build the [[HeaderValidator]]s that will be used for validating the API incoming requests.
    * This method should be called only once, at start-up of the API microservice.
    *
    * @param requiredHeaders The names of required headers
    * @return The [[HeaderValidator]]s that will be used for validating the API incoming requests.
    */
  def buildRequiredHeaderValidators(requiredHeaders: List[String]): List[HeaderValidator] = {

    val (validatedHeaders, unsupportedHeaders) = requiredHeaders.partition( headerNames.contains )

    if (unsupportedHeaders.nonEmpty) {
      throw new IllegalArgumentException(s"There are no implementations for these headers: ${unsupportedHeaders.mkString(", ")}")
    }

    headerValidators.filter ( h => validatedHeaders.contains ( h.headerName ) )
  }

  /**
    * Validate the headers of the request by using requiredHeaders.
    * This method should be called for each API incoming request.
    *
    * @param requiredHeaders The [[HeaderValidator]]s that will be used for validating the request.
    * @param request The incoming request.
    * @return Either the [[Unit]] type, for a valid request, or the errors found in the request headers.
    */
  def validate(requiredHeaders: List[HeaderValidator])(request: RequestHeader): Either[List[String], Unit] = {

    def validate: HeadersValidation = {
      requiredHeaders.map( _.validate(request) ).combineAll
    }

    validate.toEither.leftMap(_.toList)
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

      AntiFraudHeadersValidator.validate(requiredHeaderValidators)(request) match {
        case Right(_) => None
        case Left(errors: List[String]) => Some(errors).map(ErrorResponse(_))
      }

    }

  }

}
