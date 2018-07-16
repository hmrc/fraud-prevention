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

import play.api.mvc.RequestHeader

import scala.util.matching.Regex

trait RegexHeaderValidator extends HeaderValidator {

  protected def headerValueRegexPattern: Regex

  override final def isValidHeader(request: RequestHeader): Boolean = {
    hasOneHeaderValueOnly(request) && requestHeaderValues(request, headerName).forall(hasMatch)
  }

  private def hasMatch: String => Boolean = {
    headerValueRegexPattern.findFirstIn(_).nonEmpty
  }

}
