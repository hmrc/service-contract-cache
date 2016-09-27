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
import play.api.libs.ws._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

/**
  * Created by abhishek on 23/09/16.
  */
class CacheManager(cache: CacheAPI,
                   restCacheEndPoint: String,
                   cacheKey: String,
                   ws: WSClient,
                   ttl: Int) {

  def get[T]: Future[Any] = {
    cache.get(cacheKey) match {
      //Retrieve data from Cache
      case Some(data) =>
        Future.successful(data)
      //Call the endpoint and store the data in cache if successful
      case None =>
        ws.url(restCacheEndPoint)
          .get()
          .flatMap {
            response => {
              response match {
                case response if response.status == HttpStatus.InternalServer.id =>
                  Future.failed(new EndPoint500Exception(HttpStatus.InternalServer.toString))
                case response if response.status == HttpStatus.NotFound.id =>
                  Future.failed(new EndPoint404Exception(HttpStatus.NotFound.toString))
                case response if response.status == HttpStatus.NoContent.id =>
                  Future.failed(new EndPoint204Exception(HttpStatus.NoContent.toString))
                case response if response.status == HttpStatus.ContentFound.id =>
                  cache.set(cacheKey, response.body, ttl)
                  Future.successful(response.body)
                case _ =>
                  Future.failed(new EndPointAllOtherException(response.body))
              }
            }
          }
    }
  }
}

object HttpStatus extends Enumeration {
  type HttpStatus = Value
  val InternalServer = Value(500, "Internal Server Error")
  val NotFound = Value(404, "Not found")
  val NoContent = Value(204, "No content")
  val ContentFound = Value(200, "Ok")
}
