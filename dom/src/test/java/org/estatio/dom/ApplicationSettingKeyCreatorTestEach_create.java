/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.settings.ApplicationSetting;
import org.apache.isis.applib.services.settings.ApplicationSettingsServiceRW;

import org.estatio.dom.ApplicationSettingCreator.Helper;



public class ApplicationSettingKeyCreatorTestEach_create extends ApplicationSettingCreatorTestAbstract_create {

    static enum DummyApplicationSettingKey implements ApplicationSettingCreator {
        someDate(LocalDate.class, "Some date", new LocalDate(2013,4,1)),
        someInt(Integer.class, "Some integer", Integer.MAX_VALUE),
        someLong(Long.class, "Some long", Long.MAX_VALUE),
        someString(String.class, "Some string", "ABC"),
        someBoolean(Boolean.class, "Some string", Boolean.TRUE)
        ;
        
        private final Object defaultValue;
        private final String description;
        private final Class<?> dataType;
        
        private DummyApplicationSettingKey(Class<?> dataType, String description, Object defaultValue) {
            this.dataType = dataType;
            this.description = description;
            this.defaultValue = defaultValue;
        }
        @Override
        public void create(ApplicationSettingsServiceRW appSettings) {
            Helper.create(this, appSettings);
        }
        @Override
        public ApplicationSetting find(final ApplicationSettingsServiceRW appSettings) {
            return Helper.find(this, appSettings);
        }
        @Override
        public Class<?> getDataType() {
            return dataType;
        }
        @Override
        public String getDescription() {
            return description;
        }
        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }
    }
    
    public ApplicationSettingKeyCreatorTestEach_create() {
        super(DummyApplicationSettingKey.values());
    }
    
    

}
