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

package org.apache.myfaces.ext202patch.override;

import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;
import javax.faces.render.ResponseStateManager;
import org.apache.myfaces.ext202patch.renderkit.html.HtmlResponseStateManager;

/**
 * Override for default ResponseStateManager implementation
 *
 * @author Leonardo Uribe
 */
public class RenderKitWrapperImpl extends RenderKitWrapper
{
    private RenderKit _delegate;
    
    private ResponseStateManager _responseStateManager;
    
    public RenderKitWrapperImpl(RenderKit delegate)
    {
        this._delegate = delegate;
        _responseStateManager = new HtmlResponseStateManager();
    }

    @Override
    public ResponseStateManager getResponseStateManager()
    {
        return _responseStateManager;
    }
    
    @Override
    public RenderKit getWrapped()
    {
        return _delegate;
    }
    
}
