/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package ddf.catalog.registry.common.metacard;

import ddf.catalog.data.Attribute;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.data.impl.MetacardTypeImpl;

/**
 * RegistryMetacardImpl is used to provide some convenience methods for getting registry attributes
 */
public class RegistryMetacardImpl extends MetacardImpl {

    private static final RegistryObjectMetacardType ROMT = new RegistryObjectMetacardType();

    public RegistryMetacardImpl() {
        super(ROMT);
    }

    public RegistryMetacardImpl(MetacardTypeImpl type) {
        super(type);
    }

    private String getValue(String key) {
        String value = null;
        Attribute attribute = getAttribute(key);

        if (attribute != null) {
            value = (String) attribute.getValue();
        }
        return value;
    }

    public String getOrgName() {
        return getValue(RegistryObjectMetacardType.ORGANIZATION_NAME);
    }

    public String getOrgAddress() {
        return getValue(RegistryObjectMetacardType.ORGANIZATION_ADDRESS);
    }

    public String getOrgPhoneNumber() {
        return getValue(RegistryObjectMetacardType.ORGANIZATION_PHONE_NUMBER);
    }

    public String getOrgEmail() {
        return getValue(RegistryObjectMetacardType.ORGANIZATION_EMAIL);
    }

}
