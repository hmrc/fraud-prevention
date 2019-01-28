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

package uk.gov.hmrc.fraudprevention.model

import play.api.http.Status.PRECONDITION_FAILED
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{RequestHeader, Result, Results}


trait ErrorConversion {

  import Results.Status

  implicit val formatErrorResponse: OFormat[ErrorResponse] = Json.format[ErrorResponse]

  implicit def toResult(error: ErrorResponse)(implicit request: RequestHeader): Result = Status(PRECONDITION_FAILED)(Json.toJson(error))

}
