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

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELResolver;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.PropertyResolver;
import javax.faces.el.VariableResolver;

import org.apache.myfaces.buildtools.maven2.plugin.builder.annotation.JSFWebConfigParam;
import org.apache.myfaces.ov2021.config.RuntimeConfig;
import org.apache.myfaces.ov2021.el.convert.PropertyResolverToELResolver;
import org.apache.myfaces.ov2021.el.convert.VariableResolverToELResolver;
import org.apache.myfaces.shared_ext202patch.config.MyfacesConfig;

/**
 * @author Mathias Broekelmann (latest modification by $Author: lu4242 $)
 * @version $Revision: 1296050 $ $Date: 2012-03-02 05:48:51 +0100 (Fr, 02 Mär 2012) $
 */
@SuppressWarnings("deprecation")
public class ResolverBuilderBase
{
    
    private static final Logger log = Logger.getLogger(ResolverBuilderBase.class.getName());
    
    /**
     * Define a custom comparator class used to sort the ELResolvers.
     * 
     * <p>This is useful when it is necessary to put an ELResolver on top of other resolvers. Note set
     * this param override the default ordering described by JSF spec section 5. 
     * </p>
     */
    @JSFWebConfigParam(since = "1.2.10, 2.0.2", group="EL",
            desc = "The Class of an Comparator&lt;ELResolver&gt; implementation.")
    public static final String EL_RESOLVER_COMPARATOR = "org.apache.myfaces.EL_RESOLVER_COMPARATOR";
    
    private final RuntimeConfig _config;

    public ResolverBuilderBase(RuntimeConfig config)
    {
        _config = config;
    }

    /**
     * add the el resolvers from the faces config, the el resolver wrapper for variable resolver, the el resolver
     * wrapper for the property resolver and the el resolvers added by {@link Application#addELResolver(ELResolver)}.
     * The resolvers where only added if they are not null
     * 
     * @param resolvers
     */
    protected void addFromRuntimeConfig(List<ELResolver> resolvers)
    {
        if (_config.getFacesConfigElResolvers() != null)
        {
            for (ELResolver resolver : _config.getFacesConfigElResolvers())
            {
                resolvers.add(resolver);
            }
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null)
        {
            // Should not happen, but if by some reason happens,
            // initialize as usual.
            if (_config.getVariableResolver() != null)
            {
                resolvers.add(createELResolver(_config.getVariableResolver()));
            }
            else if (_config.getVariableResolverChainHead() != null)
            {
                resolvers.add(createELResolver(_config.getVariableResolverChainHead()));
            }

            if (_config.getPropertyResolver() != null)
            {
                resolvers.add(createELResolver(_config.getPropertyResolver()));
            }
            else if (_config.getPropertyResolverChainHead() != null)
            {
                resolvers.add(createELResolver(_config.getPropertyResolverChainHead()));
            }
        }
        else if (facesContext != null && MyfacesConfig.getCurrentInstance(
                facesContext.getExternalContext()).isSupportJSPAndFacesEL())
        {
            if (_config.getVariableResolver() != null)
            {
                resolvers.add(createELResolver(_config.getVariableResolver()));
            }
            else if (_config.getVariableResolverChainHead() != null)
            {
                resolvers.add(createELResolver(_config.getVariableResolverChainHead()));
            }

            if (_config.getPropertyResolver() != null)
            {
                resolvers.add(createELResolver(_config.getPropertyResolver()));
            }
            else if (_config.getPropertyResolverChainHead() != null)
            {
                resolvers.add(createELResolver(_config.getPropertyResolverChainHead()));
            }
        }

        if (_config.getApplicationElResolvers() != null)
        {
            for (ELResolver resolver : _config.getApplicationElResolvers())
            {
                resolvers.add(resolver);
            }
        }
    }
    
    /**
     * Sort the ELResolvers with a custom Comparator provided by the user.
     * @param resolvers
     * @since 1.2.10, 2.0.2
     */
    @SuppressWarnings("unchecked")
    protected void sortELResolvers(List<ELResolver> resolvers)
    {
        if (_config.getELResolverComparator() != null)
        {
            try
            {
                // sort the resolvers
                Collections.sort(resolvers, _config.getELResolverComparator());
            }
            catch (Exception e)
            {
                log.log(Level.WARNING, 
                        "Could not sort ELResolvers with custom Comparator", e);
            }
        }
    }

    protected ELResolver createELResolver(VariableResolver resolver)
    {
        return new VariableResolverToELResolver(resolver);
    }

    protected ELResolver createELResolver(PropertyResolver resolver)
    {
        return new PropertyResolverToELResolver(resolver);
    }

}
