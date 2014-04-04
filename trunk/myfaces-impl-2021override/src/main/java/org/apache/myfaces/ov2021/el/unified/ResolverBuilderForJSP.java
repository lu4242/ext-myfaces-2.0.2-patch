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
package org.apache.myfaces.ov2021.el.unified;

import java.util.ArrayList;
import java.util.List;

import javax.el.CompositeELResolver;
import javax.el.ELResolver;

import org.apache.myfaces.ov2021.config.RuntimeConfig;
import org.apache.myfaces.ov2021.el.FlashELResolver;
import org.apache.myfaces.ov2021.el.unified.resolver.ManagedBeanResolver;
import org.apache.myfaces.ov2021.el.unified.resolver.ResourceBundleResolver;
import org.apache.myfaces.ov2021.el.unified.resolver.ResourceResolver;
import org.apache.myfaces.ov2021.el.unified.resolver.implicitobject.ImplicitObjectResolver;

/**
 * build the el resolver for jsp. see 1.2 spec section 5.6.1
 * 
 * @author Mathias Broekelmann (latest modification by $Author: jakobk $)
 * @version $Revision: 985940 $ $Date: 2010-08-16 16:07:28 +0200 (Mo, 16 Aug 2010) $
 */
public class ResolverBuilderForJSP extends ResolverBuilderBase implements ELResolverBuilder
{
    public ResolverBuilderForJSP(RuntimeConfig config)
    {
        super(config);
    }

    public void build(CompositeELResolver compositeElResolver)
    {
        // add the ELResolvers to a List first to be able to sort them
        List<ELResolver> list = new ArrayList<ELResolver>();
        
        list.add(ImplicitObjectResolver.makeResolverForJSP());
        //Flash object is instanceof Map, so it is necessary to resolve
        //before MapELResolver. Better to put this one before
        list.add(new FlashELResolver());        
        list.add(new ManagedBeanResolver());
        list.add(new ResourceBundleResolver());
        list.add(new ResourceResolver());

        addFromRuntimeConfig(list);
        
        // give the user a chance to sort the resolvers
        sortELResolvers(list);
        
        // add the resolvers from the list to the CompositeELResolver
        for (ELResolver resolver : list)
        {
            compositeElResolver.add(resolver);
        }
    }

}
