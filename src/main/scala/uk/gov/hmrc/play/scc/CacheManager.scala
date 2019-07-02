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

package uk.gov.hmrc.play.scc

import play.api.cache.CacheApi
import play.api.http.Status._
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
  * Created by abhishek on 23/09/16.
  */
class CacheManager(restCacheEndPoint: String, cache: CacheApi, ws: WSClient, timeToLive: Duration,
                   headers: Seq[(String, String)]) {

  def get[T](resource: String, attribute: Option[String] = None)(implicit read: Reads[T], c: ClassTag[T]): Future[T] = {

    val cacheKey = restCacheEndPoint + "/" + resource + "/" + attribute.getOrElse("")

    val data: Option[T] = cache.get(cacheKey)

    if (data == None) {
      ws.url(cacheKey)
        .withHeaders(headers: _*)
        .get()
        .flatMap {
          case response if response.status == INTERNAL_SERVER_ERROR =>
            Future.failed(new EndPoint500Exception)
          case response if response.status == NOT_FOUND =>
            Future.failed(new EndPoint404Exception)
          case response if response.status == NO_CONTENT =>
            Future.failed(new EndPoint204Exception)
          case response if response.status == OK =>
            if (attribute.isEmpty) {
              cache.set(cacheKey, response.json.asInstanceOf[T], timeToLive)
              Future {
                response.json.validate[T].get
              }
            } else {
              val value = response.json \ attribute.get match {
                case JsDefined(res: JsValue) => {
                  res match {
                    case jsObj: JsObject => Future.successful(jsObj.asInstanceOf[T])
                    case JsNumber(n) if n.isValidInt => Future.successful(n.bigDecimal.intValue())
                    case JsString(s) => Future.successful(s)
                    case JsBoolean(b) => Future.successful(b)
                    case res => Future.failed(new UnSupportedDataType)
                  }
                }
                case _ => {
                  Future.failed(new UnSupportedDataType)
                }
              }
              value.map(value => {
                cache.set(cacheKey, value.asInstanceOf[T], timeToLive)
                value.asInstanceOf[T]
              })
            }
          case response =>
            Future.failed(new EndPointAllOtherExceptions(response.body))
        }
    } else {
      Future.successful(data.get)
    }
  }
}
