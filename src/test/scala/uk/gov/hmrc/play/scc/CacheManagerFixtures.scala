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

import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.cache.CacheAPI
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSClient, WSCookie, WSRequestHolder, WSResponse}

import scala.xml.Elem

/**
  * Created by abhishek on 26/09/16.
  */
trait CacheManagerFixtures extends MockitoSugar {
  def fixture = new {
    val mockCacheAPI = mock[CacheAPI]
    val mockCacheAPIWithCachedData = mock[CacheAPI]
    val restCacheEndPoint = "http://www.example.com"
    val cacheKey = ""
    val mockWSClient = mock[WSClient]
    val ttl = 10
    val cacheManager = new CacheManager(mockCacheAPI, mockWSClient)
    val cacheManagerWithCachedData = new CacheManager(mockCacheAPIWithCachedData, mockWSClient)
    val mockWSRequestHolder = mock[WSRequestHolder]
    val jsonMessageString =
      """
         {
          "name": "foo",
          "age": 25,
          "address": "foo bar"
          }
      """

    val jsonMessageJson = Json.parse(jsonMessageString).as[JsObject]

    when(mockCacheAPIWithCachedData.get(Matchers.any()))
      .thenReturn(Some(25))


    when(mockCacheAPI.get(Matchers.any()))
      .thenReturn(None)
    when(mockWSClient.url(Matchers.any()))
      .thenReturn(mockWSRequestHolder)

  }
}


class Response extends WSResponse {
  override def allHeaders: Map[String, Seq[String]] = ???

  override def underlying[T]: T = ???

  override def status: Int = ???

  override def statusText: String = ???

  override def header(key: String): Option[String] = ???

  override def cookies: Seq[WSCookie] = ???

  override def cookie(name: String): Option[WSCookie] = ???

  override def body: String = ???

  override def xml: Elem = ???

  override def json: JsValue = ???
}
