/*
 * Copyright 2018 Google, Inc.
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

package io.plaidapp.base.designernews.data.api.comments

import io.plaidapp.base.data.api.Result
import io.plaidapp.base.designernews.data.api.DesignerNewsService
import io.plaidapp.base.designernews.data.api.model.Comment
import java.io.IOException

/**
 * Work with the Designer News API to get comments. The class knows how to construct the requests.
 */
class DesignerNewsCommentsRemoteDataSource(private val service: DesignerNewsService) {

    /**
     * Get a list of comments based on ids from Designer News API.
     * If the response is not successful or missing, then return a null list.
     */
    suspend fun getComments(ids: List<Long>): Result<List<Comment>?> {
        val requestIds = ids.joinToString(",")
        val response = service.getComments(requestIds).await()
        return if (response.isSuccessful && response.body() != null) {
            Result.Success(response.body().orEmpty())
        } else {
            Result.Error(IOException("Error getting comments ${response.code()} " +
                    response.message()))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: DesignerNewsCommentsRemoteDataSource? = null

        fun getInstance(service: DesignerNewsService): DesignerNewsCommentsRemoteDataSource {
            return INSTANCE
                    ?: synchronized(this) {
                        INSTANCE ?: DesignerNewsCommentsRemoteDataSource(
                                service
                        ).also { INSTANCE = it }
                    }
        }
    }
}