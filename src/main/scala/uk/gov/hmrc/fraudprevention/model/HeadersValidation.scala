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

package uk.gov.hmrc.fraudprevention.model

import cats.data.ValidatedNel

object HeadersValidation {

  // At the moment we are not interested in the header name-values, thus we return `Unit` in case of success.
  // But we could use ValidatedNel[String, Map[String, List[String]],
  // where the successful case returns the list of values for each single validated header
  type HeadersValidation = ValidatedNel[String, Unit]

}
