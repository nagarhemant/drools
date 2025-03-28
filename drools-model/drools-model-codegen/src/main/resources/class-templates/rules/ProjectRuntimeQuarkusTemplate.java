/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.project.model;

import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.drools.modelcompiler.KieBaseBuilder;

@javax.enterprise.context.ApplicationScoped
public class ProjectRuntime implements KieRuntimeBuilder {

    private static final ProjectModel model = new ProjectModel();
    private static final java.util.Map<String, KieBase> kbases = initKieBases();

    public static final ProjectRuntime INSTANCE = new ProjectRuntime();

    private static java.util.Map<String, KieBase> initKieBases() {
        java.util.Map<String, KieBase> kbaseMap = new ConcurrentHashMap<>();
        if (org.drools.core.util.Drools.isNativeImage()) {
        }
        return kbaseMap;
    }

    @Override
    public KieBase getKieBase() {
        return getKieBase("$defaultKieBase$");
    }

    @Override
    public KieBase getKieBase(String name) {
        return kbases.computeIfAbsent( name, n -> KieBaseBuilder.createKieBaseFromModel(model.getModelsForKieBase(n), model.getKieModuleModel().getKieBaseModels().get(n)) );
    }

    @Override
    public KieSession newKieSession() {
        return newKieSession("$defaultKieSession$");
    }

    @Override
    public KieSession newKieSession(String sessionName) {
        KieBase kbase = getKieBaseForSession(sessionName);
        if (kbase == null) {
            throw new RuntimeException("Unknown KieSession with name '" + sessionName + "'");
        }
        KieSession ksession = kbase.newKieSession(getConfForSession(sessionName), null);
        return ksession;
    }

    private KieBase getKieBaseForSession(String sessionName) {
        switch(sessionName) {
            // populated via codegen
        }
        return null;
    }

    private org.kie.api.runtime.KieSessionConfiguration getConfForSession(String sessionName) {
        org.drools.core.SessionConfigurationImpl conf = new org.drools.core.SessionConfigurationImpl();
        switch(sessionName) {
            // populated via codegen
        }
        return conf;
    }
}
