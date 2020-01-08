/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.paging

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

abstract class PagedDataAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : RecyclerView.Adapter<VH>() {
    private val differ = AsyncPagedDataDiffer(
        mainDispatcher = mainDispatcher,
        diffCallback = diffCallback,
        updateCallback = AdapterListUpdateCallback(this)
    )

    fun connect(flow: Flow<PagedData<T>>, scope: CoroutineScope) {
        differ.connect(flow, scope)
    }

    fun retry() {
        differ.retry()
    }

    protected open fun getItem(position: Int) = differ.getItem(position)

    override fun getItemCount() = differ.itemCount

    /**
     * Add a [LoadState] listener to observe the loading state of the current [PagedData].
     *
     * As new PagedData generations are submitted and displayed, the listener will be notified to
     * reflect current [LoadType.REFRESH], [LoadType.START], and [LoadType.END] states.
     *
     * @param listener [LoadState] listener to receive updates.
     *
     * @see removeLoadStateListener
     */
    open fun addLoadStateListener(listener: (LoadType, LoadState) -> Unit) {
        differ.addLoadStateListener(listener)
    }

    /**
     * Remove a previously registered [LoadState] listener.
     *
     * @param listener Previously registered listener.
     * @see addLoadStateListener
     */
    open fun removeLoadStateListener(listener: (LoadType, LoadState) -> Unit) {
        differ.removeLoadStateListener(listener)
    }
}