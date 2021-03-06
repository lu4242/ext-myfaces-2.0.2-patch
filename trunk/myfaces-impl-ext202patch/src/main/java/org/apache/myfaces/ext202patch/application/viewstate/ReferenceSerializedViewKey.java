/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.ext202patch.application.viewstate;

import java.io.Serializable;

/**
 * Implementation of SerializedViewKey, where the viewId and the sequenceId can be
 * anything.
 */
class ReferenceSerializedViewKey<I, K> extends SerializedViewKey implements Serializable
{
    private static final long serialVersionUID = -1170697124386063642L;
    final I _viewId;
    final K _sequenceId;

    public ReferenceSerializedViewKey()
    {
        _sequenceId = null;
        _viewId = null;
    }

    public ReferenceSerializedViewKey(I viewId, K sequence)
    {
        _sequenceId = sequence;
        _viewId = viewId;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final ReferenceSerializedViewKey<I, K> other = (ReferenceSerializedViewKey<I, K>) obj;
        if (this._viewId != other._viewId && (this._viewId == null || !this._viewId.equals(other._viewId)))
        {
            return false;
        }
        if (this._sequenceId != other._sequenceId && (this._sequenceId == null || 
            !this._sequenceId.equals(other._sequenceId)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 83 * hash + (this._viewId != null ? this._viewId.hashCode() : 0);
        hash = 83 * hash + (this._sequenceId != null ? this._sequenceId.hashCode() : 0);
        return hash;
    }
    
}
