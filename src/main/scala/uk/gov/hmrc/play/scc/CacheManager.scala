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

import play.api.cache.CacheAPI
import play.api.http.Status._
import play.api.libs.json._
import play.api.libs.ws._

import scala.language.implicitConversions
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by abhishek on 23/09/16.
  */
class CacheManager(cache: CacheAPI, ws: WSClient) {

  def get[T](restCacheEndPoint: String, cacheKey: String, ttl: Int): Future[T] = {
    cache.get(cacheKey) match {
      //Retrieve data from Cache
      case Some(data) =>
        Future.successful(data.asInstanceOf[T])
      //Call the endpoint and store the data in cache if successful
      case None =>
        ws.url(restCacheEndPoint)
          .get()
          .flatMap {
            case response if response.status == INTERNAL_SERVER_ERROR =>
              Future.failed(new EndPoint500Exception)
            case response if response.status == NOT_FOUND =>
              Future.failed(new EndPoint404Exception)
            case response if response.status == NO_CONTENT =>
              Future.failed(new EndPoint204Exception)
            case response if response.status == OK =>
              val value = (response.json \ cacheKey) match {
                case JsNumber(n) if (n.isValidInt) => Future.successful(n.bigDecimal.intValue())
                case JsString(s) => Future.successful(s)
                case JsBoolean(b) => Future.successful(b)
                case _ =>
                  Future.failed(new UnSupportedDataType)
              }
              value.map(value => {
                cache.set(cacheKey, value.asInstanceOf[T], ttl)
                value.asInstanceOf[T]
              })
            case response =>
              Future.failed(new EndPointAllOtherException(response.body))
          }
    }
  }
}