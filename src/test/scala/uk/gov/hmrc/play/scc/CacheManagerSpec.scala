/*
 * Copyright 2016 HM Revenue & Customs
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

package uk.gov.hmrc.play.scc

import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.JsNumber

import scala.concurrent.Future

/**
  * Created by abhishek on 23/09/16.
  */
class CacheManagerSpec extends FlatSpec
  with Matchers
  with ScalaFutures
  with MockitoSugar
  with CacheManagerFixtures {
  val f = fixture

  import f._

  "CacheManager#get" should "return EndPointNotFoundException when endpoint returned '500'" in {

    when(mockWSRequestHolder.get())
      .thenReturn(Future.successful(new Response {
        override def status = 500
      }))

    val cacheResult = cacheManager.get[Int]("www.example.com", "key", 100)
    cacheResult.failed.futureValue shouldBe a[EndPoint500Exception]
  }

  "CacheManager#get" should "return EndPointInternalServerErrorException when endpoint returned '404 - Not Found'" in {
    when(mockWSRequestHolder.get())
      .thenReturn(Future.successful(new Response {
        override def status = 404
      }))

    val cacheResult = cacheManager.get[Int]("www.example.com", "key", 100)
    cacheResult.failed.futureValue shouldBe a[EndPoint404Exception]
  }

  "CacheManager#get" should "return EndPointNoContentException when endpoint returned '204 - No Content'" in {
    when(mockWSRequestHolder.get())
      .thenReturn(Future.successful(new Response {
        override def status = 204
      }))

    val cacheResult = cacheManager.get[Int]("www.example.com", "key", 100)
    cacheResult.failed.futureValue shouldBe a[EndPoint204Exception]
  }

  "CacheManager#get" should "return EndPointAllOtherException when endpoint returned '503 - Service Unavailable'" in {
    when(mockWSRequestHolder.get())
      .thenReturn(Future.successful(new Response {
        override def status = 503

        override def body = "Service Unavailable"
      }))

    val cacheResult = cacheManager.get[String]("www.example.com", "key", 100)
    cacheResult.failed.futureValue shouldBe a[EndPointAllOtherException]
    cacheResult.failed.futureValue.getMessage shouldBe "Service Unavailable"

  }

  "CacheManager#get" should "return body content when endpoint returned '200 - Ok'" in {
    when(mockWSRequestHolder.get())
      .thenReturn(Future.successful(new Response {
        override def status = 200

        override def json = jsonMessageJson
      }))

    val cacheResultInt = cacheManager.get[String]("www.example.com", "name", 100)
    whenReady(cacheResultInt) {
      res => res shouldBe "foo"
    }

    val cacheResultString = cacheManager.get[Int]("www.example.com", "age", 100)
    whenReady(cacheResultString) {
      res =>
        res shouldBe 25
    }

    val cacheResultBoolean = cacheManager.get[Boolean]("www.example.com", "isMinor", 100)
    whenReady(cacheResultBoolean) {
      res =>
        res shouldBe false
    }

    val cacheResultException = cacheManager.get[Int]("www.example.com", "address", 100)
    cacheResultException.failed.futureValue shouldBe a[UnSupportedDataType]

  }

  "CacheManager#get" should "return content from Cache" in {

    val cacheResult = cacheManagerWithCachedData.get[Int]("www.example.com", "age", 100)
    whenReady(cacheResult) {
      res => res shouldBe 25
    }
  }
}
