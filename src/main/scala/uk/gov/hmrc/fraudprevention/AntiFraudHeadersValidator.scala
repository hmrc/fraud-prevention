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

package uk.gov.hmrc.fraudprevention

import cats.implicits._
import play.api.mvc._
import uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator
import uk.gov.hmrc.fraudprevention.headervalidators.impl._
import uk.gov.hmrc.fraudprevention.model.HeadersValidation.HeadersValidation
import uk.gov.hmrc.fraudprevention.model.{ErrorConversion, ErrorResponse}

import scala.concurrent.Future

/**
  * Object for validating the fraud prevention headers.
  */
object AntiFraudHeadersValidator {

  private lazy val headerValidators: List[HeaderValidator] = List(
    GovClientPublicPortHeaderValidator, GovClientColourDepthHeaderValidator, GovClientDeviceIdHeaderValidator,
    GovClientUserIdHeaderValidator, GovClientTimezoneHeaderValidator, GovClientLocalIpHeaderValidator,
    GovVendorVersionHeaderValidator, GovVendorLicenseIdHeaderValidator, GovClientConnectionMethodHeaderValidator
  )

  private lazy val headerNames: List[String] = headerValidators.map(_.headerName)

  /**
    * Builds the [[uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator HeaderValidator]]s that will be used for validating the API incoming requests.
    * This method should be called only once, at start-up of the API microservice.
    *
    * @param requiredHeaders The names of required fraud prevention headers.
    * @return The [[uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator HeaderValidator]]s that will be used for validating the API incoming requests.
    */
  def buildRequiredHeaderValidators(requiredHeaders: List[String]): List[HeaderValidator] = {

    val (validatedHeaders, unsupportedHeaders) = requiredHeaders.partition( headerNames.contains )

    if (unsupportedHeaders.nonEmpty) {
      throw new IllegalArgumentException(s"There are no implementations for these headers: ${unsupportedHeaders.mkString(", ")}")
    }

    headerValidators.filter ( h => validatedHeaders.contains ( h.headerName ) )
  }

  /**
    * Validates the headers of the request by using the given [[uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator HeaderValidator]]s.
    * This method should be called for each API incoming request.
    *
    * @param requiredHeaders The [[uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator HeaderValidator]]s that will be used for validating the request.
    * @param request The incoming request.
    * @return Either the [[scala.Unit Unit]] type if the request has valid headers, the errors found otherwise.
    */
  def validate(requiredHeaders: List[HeaderValidator])(request: RequestHeader): Either[List[String], Unit] = {

    def validate: HeadersValidation = {
      requiredHeaders.map( _.validate(request) ).combineAll
    }

    validate.toEither.leftMap(_.toList)
  }

}

/**
  * Factory for creating Play [[play.api.mvc.ActionFilter ActionFilter]]s that validate incoming requests using
  * [[uk.gov.hmrc.fraudprevention.AntiFraudHeadersValidator AntiFraudHeadersValidator]].
  */
object AntiFraudHeadersValidatorActionFilter extends ErrorConversion {

  private lazy val defaultHeaderValidators: List[HeaderValidator] = List(
    GovClientDeviceIdHeaderValidator, GovClientUserIdHeaderValidator, GovClientTimezoneHeaderValidator,
    GovClientLocalIpHeaderValidator, GovVendorVersionHeaderValidator, GovVendorLicenseIdHeaderValidator,
    GovClientConnectionMethodHeaderValidator
  )

  /**
    *
    * Creates an [[play.api.mvc.ActionFilter ActionFilter]] that validates API incoming requests using the default
    * [[uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator HeaderValidator]]s.
    *
    * @return The [[play.api.mvc.ActionFilter ActionFilter]].
    */
  def actionFilterWithDefaultHeaders: ActionBuilder[Request] with ActionFilter[Request] = {
    actionFilterFromHeaderValidators(defaultHeaderValidators)
  }

  /**
    * Creates an [[play.api.mvc.ActionFilter ActionFilter]] that validates API incoming requests depending on the given required headers.
    *
    * @param requiredHeaders The names of the required headers.
    * @return The [[play.api.mvc.ActionFilter ActionFilter]].
    */
  def actionFilterFromHeaderNames(requiredHeaders: List[String]): ActionBuilder[Request] with ActionFilter[Request] = {
    val requiredHeaderValidators = AntiFraudHeadersValidator.buildRequiredHeaderValidators(requiredHeaders)
    actionFilterFromHeaderValidators(requiredHeaderValidators)
  }

  /**
    * Creates an [[play.api.mvc.ActionFilter ActionFilter]] that validates API incoming requests using the given
    * [[uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator HeaderValidator]]s.
    *
    * @param requiredHeaderValidators The [[uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator HeaderValidator]]s
    *                                 to be used for validating the incoming requests.
    * @return The [[play.api.mvc.ActionFilter ActionFilter]].
    */
  def actionFilterFromHeaderValidators(requiredHeaderValidators: List[HeaderValidator]): ActionBuilder[Request] with ActionFilter[Request] =
    new ActionBuilder[Request] with ActionFilter[Request] {

    override protected def filter[A](request: Request[A]): Future[Option[Result]] = Future.successful {

      implicit val r: Request[A] = request

      AntiFraudHeadersValidator.validate(requiredHeaderValidators)(request) match {
        case Right(_) => None
        case Left(errors: List[String]) => Some(errors).map(ErrorResponse(_))
      }

    }

  }

}
