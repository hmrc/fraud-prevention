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

import play.api.Logger
import play.api.http.Status.PRECONDITION_FAILED
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._
import uk.gov.hmrc.fraudprevention.headervalidators.{HeaderValidator, HeaderValidatorUtils}
import uk.gov.hmrc.fraudprevention.headervalidators.impl._

import scala.concurrent.Future
import scala.util.{Failure, Try}

case class ErrorResponse(errorCode: String, message: String)

case object ErrorResponse {

  def buildErrorMessage(errorMsg: String): ErrorResponse = {
    ErrorResponse("MISSING_OR_INVALID_HEADERS", errorMsg)
  }

}


trait ErrorConversion {

  import Results._

  implicit val formatErrorResponse: OFormat[ErrorResponse] = Json.format[ErrorResponse]

  implicit def toResult(error: ErrorResponse)(implicit request: RequestHeader): Result = Status(PRECONDITION_FAILED)(Json.toJson(error))
}


trait AntiFraudHeadersValidator extends ErrorConversion with HeaderValidatorUtils {

  // TODO: improve - use predicates ?!
  private def getHeaderValidator: String => HeaderValidator = {
    case "Gov-Client-Public-IP"   => GovClientPublicIpHeaderValidator
    case "Gov-Client-Local-IP"    => GovClientLocalIpHeaderValidator
    case "Gov-Client-Public-Port" => GovClientPublicPortHeaderValidator
    case "Gov-Client-Device-ID"   => GovClientDeviceIdHeaderValidator
  }

  private def isMissingImplementation: String => Boolean = { str =>
    Try ( getHeaderValidator(str) ) match {
      case Failure(_) => true
      case _ => false
    }
  }

  def retrieveErrors(requiredHeaders: List[String], request: RequestHeader): Option[String] = {

    val missingImplementations = requiredHeaders.filter ( isMissingImplementation )

    if (missingImplementations.nonEmpty) {
      Logger.warn(s"There are no validator implementations for these headers: ${missingImplementations.mkString(", ")}")
    }

//  TODO: use cats with applicative errors

    val missingOrInvalidHeaderValues: List[String] = requiredHeaders.filterNot(isMissingImplementation)
      .filterNot(getHeaderValidator(_).isValidHeader(request))

    missingOrInvalidHeaderValues match {
      case Nil => None
      case errors: List[String] => Some(s"Missing or invalid headers: ${errors.mkString(", ")}")
    }

  }

}




object AntiFraudHeadersValidator extends AntiFraudHeadersValidator {

  def fraudPreventionFilter[A] (requiredHeaders: List[String]) = new ActionBuilder[Request] with ActionFilter[Request] {

    // TODO: fix warning type parameter
    override protected def filter[A](request: Request[A]): Future[Option[Result]] = Future.successful {
      implicit val r: Request[A] = request
      retrieveErrors(requiredHeaders, request).map(ErrorResponse.buildErrorMessage)
    }

  }

}

