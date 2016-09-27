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
import play.api.libs.ws._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by abhishek on 23/09/16.
  */
class CacheManager(cache: CacheAPI, ws: WSClient) {
  def get(restCacheEndPoint: String, cacheKey: String, ttl: Int): Future[String] = {
    cache.get(cacheKey) match {
      //Retrieve data from Cache
      case Some(data: String) =>
        Future.successful(data)
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
              cache.set(cacheKey, response.body, ttl)
              Future.successful(response.body)
            case response =>
              Future.failed(new EndPointAllOtherException(response.body))
          }
    }
  }
}