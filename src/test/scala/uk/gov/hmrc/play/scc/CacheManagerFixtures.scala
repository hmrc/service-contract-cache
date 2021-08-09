/*
 * Copyright 2021 HM Revenue & Customs
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

import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.cache.SyncCacheApi
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSCookie, WSRequest, WSResponse}
import scala.concurrent.duration._
import scala.xml.Elem


trait CacheManagerFixtures extends MockitoSugar {
  def fixture = new {
    val mockCacheAPI = mock[SyncCacheApi]
    val mockCacheAPIWithCachedData = mock[SyncCacheApi]
    val restCacheEndPoint = "http://www.example.com"
    val cacheKey = ""
    val mockWSClient = mock[WSClient]
    val ttl = 10 seconds

    val cacheManager = new CacheManager(restCacheEndPoint, mockCacheAPI, mockWSClient, ttl, Seq(("ACCEPT", "text/html")))
    val cacheManagerWithCachedData = new CacheManager(restCacheEndPoint, mockCacheAPIWithCachedData,
      mockWSClient, ttl, Seq(("ACCEPT", "text/html")))


    val mockWSRequestHolderTemp = mock[WSRequest]
    val mockWSRequestHolder = mock[WSRequest]
    val jsonMessageString =
      """
         {
          "name": "foo",
          "age": 25,
          "isMinor": false,
          "address": ["foo", "bar"],
          "salary": {
                    "amount" : "100",
                    "curr" : "foo"
                    }
        }
      """

    implicit val fooBarReads: Reads[FooBar] = (
      (JsPath \ "name").read[String] and
        (JsPath \ "age").read[Int] and
        (JsPath \ "isMinor").read[Boolean]
      ) (FooBar.apply _)

    val jsonMessageJson = Json.parse(jsonMessageString).as[JsObject]

    when(mockWSClient.url(ArgumentMatchers.any()))
      .thenReturn(mockWSRequestHolderTemp)

    when(mockWSRequestHolderTemp.withHttpHeaders(ArgumentMatchers.any()))
      .thenReturn(mockWSRequestHolder)
  }
}

case class FooBar(name: String, age: Int, isMinor: Boolean)

trait Response extends WSResponse {
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

  override def bodyAsBytes: ByteString = ???

  override def headers: Map[String, Seq[String]] = ???

  override def bodyAsSource: Source[ByteString, _] = ???

  def uri = ???
}
