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

import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewDeclarationLanguageFactory;
import org.apache.myfaces.view.facelets.FaceletViewDeclarationLanguage;

/**
 *
 * @author lu4242
 */
public class ViewDeclarationLanguageFixedFactory extends ViewDeclarationLanguageFactory
{
    
    private ViewDeclarationLanguageFactory delegate;
    
    private ViewDeclarationLanguage faceletsVdl;
    
    //public ViewDeclarationLanguageFixedFactory()
    //{
    //    this.delegate = new ViewDeclarationLanguageFactoryImpl();
    //}
    
    public ViewDeclarationLanguageFixedFactory(ViewDeclarationLanguageFactory delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public ViewDeclarationLanguage getViewDeclarationLanguage(String string)
    {
        ViewDeclarationLanguage vdl = delegate.getViewDeclarationLanguage(string);
        
        if (vdl instanceof FaceletViewDeclarationLanguage)
        {
            if (faceletsVdl == null)
            {
                synchronized(this)
                {
                    if (faceletsVdl == null)
                    {
                        faceletsVdl = new FaceletFixStateViewDeclarationLanguage(vdl);
                    }
                }
            }
            return faceletsVdl;
        }
        return vdl;
    }
}
