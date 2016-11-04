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

import akka.util.ByteString
import org.mockito.{ArgumentMatchers, Matchers}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.cache.CacheApi
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSCookie, WSRequest, WSResponse}
import play.api.libs.functional.syntax._

import scala.xml.Elem

/**
  * Created by abhishek on 26/09/16.
  */
trait CacheManagerFixtures extends MockitoSugar {
  def fixture = new {
    val mockCacheAPI = mock[CacheApi]
    val mockCacheAPIWithCachedData = mock[CacheApi]
    val restCacheEndPoint = "http://www.example.com"
    val cacheKey = ""
    val mockWSClient = mock[WSClient]
    val ttl = 10
    val cacheManager = new CacheManager(restCacheEndPoint, mockCacheAPI, mockWSClient, ttl)
    val cacheManagerWithCachedData = new CacheManager(restCacheEndPoint, mockCacheAPIWithCachedData, mockWSClient, ttl)
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

     when(mockCacheAPIWithCachedData.get(ArgumentMatchers.any()))
      .thenReturn(Some(25))


    when(mockCacheAPI.get(ArgumentMatchers.any()))
      .thenReturn(None)
    when(mockWSClient.url(ArgumentMatchers.any()))
      .thenReturn(mockWSRequestHolder)

  }
}

case class FooBar(name: String, age: Int, isMinor: Boolean)

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

  override def bodyAsBytes: ByteString = ???
}
