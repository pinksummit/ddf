/**
 * Copyright (c) Codice Foundation
 * <p/>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.spatial.ogc.csw.catalog.transformer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.spatial.ogc.csw.catalog.common.GmdMetacardType;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.MetacardImpl;

public class TestGmdTransformer {

    private GmdTransformer gmdTransformer;

    private static final TreeSet<String> SUBJECTS = new TreeSet<>(Arrays.asList("Geologie",
            "World",
            "boundaries",
            "elevation",
            "inlandWaters",
            "oceans",
            "society",
            "structure",
            "transportation",
            "utilitiesCommunication"));

    private void assertGmdMetacard(Metacard metacard) {
        assertThat(metacard.getMetacardType()
                .getName(), is(GmdMetacardType.GMD_METACARD_TYPE_NAME));
    }

    @Before
    public void setUp() {
        gmdTransformer = new GmdTransformer();
    }

    @Test(expected = IOException.class)
    public void testBadInputStream() throws Exception {
        InputStream is = Mockito.mock(InputStream.class);
        doThrow(new IOException()).when(is)
                .read(any());
        gmdTransformer.transform(is);
    }

    @Test
    public void testDataset() throws Exception {
        Metacard metacard = transform("/gmd/dataset.xml");
        assertGmdMetacard(metacard);
        DateTimeFormatter dateFormatter = ISODateTimeFormat.dateOptionalTimeParser();
        Date expectedDate = dateFormatter.parseDateTime("2004-03-14")
                .toDate();

        assertThat(metacard.getId(), is("550e8400-e29b-41d4-a716-48957441234"));

        assertThat(metacard.getContentTypeName(), is("dataset"));

        assertThat(metacard.getModifiedDate(), is(expectedDate));
        assertThat(metacard.getCreatedDate(), is(expectedDate));

        assertThat(metacard.getAttribute(GmdMetacardType.GMD_CRS)
                .getValue(), is("urn:ogc:def:crs:World Geodetic System::WGS 84"));

        assertThat(metacard.getTitle(), is("VMAPLV0"));
        assertThat(metacard.getAttribute(Metacard.DESCRIPTION)
                .getValue(), is(
                "Vector Map: a general purpose database design to support GIS applications"));

        TreeSet<String> subjectAttributes = new TreeSet<>();
        metacard.getAttribute(GmdMetacardType.GMD_SUBJECT)
                .getValues()
                .forEach(subject -> subjectAttributes.add((String) subject));
        assertThat(subjectAttributes, is(SUBJECTS));

        assertThat(metacard.getResourceURI()
                .toASCIIString(), is("http://example.com"));
        assertThat(metacard.getLocation(), is(
                "POLYGON ((6.9 -44.94, 6.9 61.61, 70.35 61.61, 70.35 -44.94, 6.9 -44.94))"));

        assertThat(metacard.getAttribute(Metacard.POINT_OF_CONTACT).getValue(), is("example organization"));
        assertThat(metacard.getAttribute(GmdMetacardType.GMD_PUBLISHER).getValue(), is("example organization"));

    }

    @Test
    public void testFormat() throws Exception {
        Metacard metacard = transform("/gmd/dataset2.xml");
        assertGmdMetacard(metacard);

        assertThat(metacard.getAttribute(GmdMetacardType.GMD_FORMAT)
                .getValue(), is("shapefile"));
    }

    @Test
    public void testAllMetadata() throws Exception {
        InputStream input = getClass().getResourceAsStream("/gmd/");
        try (BufferedReader rdr = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = rdr.readLine()) != null) {

                if (StringUtils.isNotEmpty(line) && line.endsWith(".xml")) {
                    Metacard metacard = transform("/gmd/" + line);
                    assertGmdMetacard(metacard);
                }
            }
        }

    }

    private MetacardImpl transform(String path) throws Exception {
        return (MetacardImpl) gmdTransformer.transform(getClass().getResourceAsStream(path));
    }
}
