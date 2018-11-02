/*
 * Copyright 2016-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.joinfaces.autoconfigure.servlet.initializer;

import javax.servlet.ServletContainerInitializer;

import io.github.classgraph.ClassGraph;

/**
 * A special {@link ServletContainerInitializerRegistrationBean} which for Mojarra and MyFaces
 * which applies additional filters to {@link ClassGraph} in order to improve the scan speed.
 *
 * @param <T> Type of the actual {@link ServletContainerInitializer} implementation
 * @author Lars Grefer
 */
public class JsfServletContainerInitializerRegistrationBean<T extends ServletContainerInitializer> extends ServletContainerInitializerRegistrationBean<T> {
	public JsfServletContainerInitializerRegistrationBean(Class<T> servletContainerInitializerClass) {
		super(servletContainerInitializerClass);
	}

	private boolean filterJars(String path) {
		if (!path.endsWith(".jar")) {
			return true;
		}

		int index = path.lastIndexOf('/');

		String jarName = path.substring(index + 1);

		if (jarName.startsWith("junit")) {
			return false;
		}

		if (jarName.startsWith("logback") || jarName.contains("slf4j")) {
			return false;
		}

		if (jarName.startsWith("tomcat-embed")) {
			return jarName.startsWith("tomcat-embed-websocket");
		}

		if (jarName.startsWith("classgraph")) {
			return false;
		}

		if (jarName.startsWith("spring")) {
			if (jarName.startsWith("spring-web")) {
				return !jarName.startsWith("spring-webmvc");
			}
		}
		return true;
	}

	protected ClassGraph prepareClassgraph(ClassGraph classGraph) {
		return super.prepareClassgraph(classGraph)
				.blacklistPackages("io.github.classgraph")
				.blacklistPackages("org.springframework.boot", "org.yaml.snakeyaml")
				.blacklistPackages("net.bytebuddy", "org.eclipse.jdt.internal")
				.blacklistPackages("org.apache.catalina", "org.eclipse.jetty", "io.undertow")
				.blacklistPackages("org.slf4j", "ch.qos.logback", "org.apache.logging.log4j")
				.blacklistPackages("com.fasterxml.jackson")
				.filterClasspathElements(this::filterJars);
	}
}
