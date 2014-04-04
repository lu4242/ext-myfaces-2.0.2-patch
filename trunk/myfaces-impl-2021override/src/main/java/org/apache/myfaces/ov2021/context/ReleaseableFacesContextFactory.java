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
package org.apache.myfaces.ov2021.context;

/**
 *
 * @author  Leonardo Uribe (latest modification by $Author: lu4242 $)
 * @version $Revision: 799765 $ $Date: 2009-08-01 00:55:49 +0200 (Sa, 01 Aug 2009) $
 */
public interface ReleaseableFacesContextFactory
{
    /**
     * Release resources that the ExternalContext is holding onto.
     */
    public void release();
}
