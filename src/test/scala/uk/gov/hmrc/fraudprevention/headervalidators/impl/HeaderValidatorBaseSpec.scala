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

package uk.gov.hmrc.fraudprevention.headervalidators.impl

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

import play.api.mvc.{AnyContent, Request}
import play.api.test.FakeRequest
import uk.gov.hmrc.fraudprevention.headervalidators.HeaderValidator
import uk.gov.hmrc.play.test.UnitSpec

// TODO: add integration test for testing the Play thing !?

trait HeaderValidatorBaseSpec extends UnitSpec {

  protected def buildRequest(headerValues: Option[Seq[String]], headerName: String): Request[AnyContent] = {

    // TODO: write it better
    headerValues match {
      case None => FakeRequest()
      case Some(headerValues: Seq[String]) => FakeRequest().withHeaders ( headerValues.map(headerName -> _): _* )
    }

  }

  protected def headerValidator: HeaderValidator

  protected def validate(headerValues: Option[Seq[String]]): Boolean = {
    headerValidator.isValidHeader(buildRequest(headerValues, headerValidator.headerName))
  }

}
