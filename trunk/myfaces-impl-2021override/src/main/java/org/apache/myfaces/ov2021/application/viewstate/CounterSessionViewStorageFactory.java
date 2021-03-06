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
package org.apache.myfaces.ov2021.application.viewstate;

import javax.faces.context.FacesContext;

/**
 *
 * @author lu4242
 */
class CounterSessionViewStorageFactory extends SessionViewStorageFactory
    <KeyFactory<Integer, String>, Integer, String>
{

    public CounterSessionViewStorageFactory(KeyFactory<Integer, String> keyFactory)
    {
        super(keyFactory);
    }

    @Override
    public SerializedViewCollection createSerializedViewCollection(FacesContext context)
    {
        return new SerializedViewCollection();
    }

    @Override
    public SerializedViewKey createSerializedViewKey(FacesContext context, String viewId, Integer key)
    {
        return new IntIntSerializedViewKey(viewId == null ? 0 : viewId.hashCode(), key);
    }
    
}
