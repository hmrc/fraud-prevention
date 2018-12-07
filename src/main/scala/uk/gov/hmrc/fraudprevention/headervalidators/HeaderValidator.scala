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

import cats.implicits._
import play.api.mvc.RequestHeader
import uk.gov.hmrc.fraudprevention.model.HeadersValidation.HeadersValidation

trait HeaderValidatorUtils {

  protected final def requestHeaderValues(request: RequestHeader, headerName: String): List[String] = {
    request.headers.toMap.getOrElse(headerName, Nil).toList
  }

}

trait HeaderValidator extends HeaderValidatorUtils {

  def headerName: String

  protected final def validateHeaderIsUnique(request: RequestHeader): Either[String, String] = {
    // We are assuming that headers must have at most one value,
    // but for some headers we might want to allow multiple values.
    requestHeaderValues(request, headerName) match {
      case Nil => Left(s"")
      case headerValue :: Nil => Right(headerValue)
      case _ => Left(s"Multiple values for header $headerName")
    }
  }

  protected def validateHeaderValue(headerValue: String): Either[String, Unit]

  final def validate(request: RequestHeader): HeadersValidation = {
    validateHeaderIsUnique(request).flatMap(validateHeaderValue).toValidatedNel
  }

}
