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

import java.beans.BeanInfo;
import java.io.IOException;
import java.util.List;
import javax.faces.FacesWrapper;
import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.AttachedObjectHandler;
import javax.faces.view.StateManagementStrategy;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewMetadata;

/**
 *
 * @author lu4242
 */
public class FaceletFixStateViewDeclarationLanguage extends ViewDeclarationLanguage 
    implements FacesWrapper<ViewDeclarationLanguage>
{
    
    private ViewDeclarationLanguage delegate;
    
    private StateManagementStrategy _stateMgmtStrategy;

    public FaceletFixStateViewDeclarationLanguage(ViewDeclarationLanguage delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public void buildView(FacesContext fc, UIViewRoot uivr) throws IOException
    {
        getWrapped().buildView(fc, uivr);
    }

    @Override
    public UIViewRoot createView(FacesContext fc, String string)
    {
        return getWrapped().createView(fc, string);
    }

    @Override
    public BeanInfo getComponentMetadata(FacesContext fc, Resource rsrc)
    {
        return getWrapped().getComponentMetadata(fc, rsrc);
    }

    @Override
    public Resource getScriptComponentResource(FacesContext fc, Resource rsrc)
    {
        return getWrapped().getScriptComponentResource(fc, rsrc);
    }

    @Override
    public StateManagementStrategy getStateManagementStrategy(FacesContext fc, String string)
    {
        StateManagementStrategy strategy = getWrapped().getStateManagementStrategy(fc, string);
        if (strategy != null)
        {
            if (_stateMgmtStrategy == null)
            {
                _stateMgmtStrategy = new StateFixDefaultFaceletsStateManagementStrategy();
            }
            return _stateMgmtStrategy;
        }
        return strategy; 
    }

    @Override
    public ViewMetadata getViewMetadata(FacesContext fc, String string)
    {
        return getWrapped().getViewMetadata(fc, string);
    }

    @Override
    public void renderView(FacesContext fc, UIViewRoot uivr) throws IOException
    {
        getWrapped().renderView(fc, uivr);
    }

    @Override
    public UIViewRoot restoreView(FacesContext fc, String string)
    {
        return getWrapped().restoreView(fc, string);
    }

    @Override
    public void retargetAttachedObjects(FacesContext fc, UIComponent uic, List<AttachedObjectHandler> list)
    {
        getWrapped().retargetAttachedObjects(fc, uic, list);
    }

    @Override
    public void retargetMethodExpressions(FacesContext fc, UIComponent uic)
    {
        getWrapped().retargetMethodExpressions(fc, uic);
    }

    public ViewDeclarationLanguage getWrapped()
    {
        return delegate;
    }
    
}
