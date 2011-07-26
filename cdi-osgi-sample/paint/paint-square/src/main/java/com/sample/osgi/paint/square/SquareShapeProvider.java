/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sample.osgi.paint.square;

import com.sample.osgi.paint.api.Shape;
import com.sample.osgi.paint.api.ShapeProvider;
import org.osgi.cdi.api.extension.annotation.Publish;
import org.osgi.cdi.api.extension.annotation.Sent;
import org.osgi.cdi.api.extension.annotation.Specification;
import org.osgi.cdi.api.extension.events.InterBundleEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@Publish
@ApplicationScoped
public class SquareShapeProvider implements ShapeProvider {

    @Override
    public Shape getShape() {
        return new Square();
    }

    @Override
    public String getId() {
        return Square.class.getName();
    }

    public void listen(@Observes @Sent @Specification(String.class) InterBundleEvent message) {
        System.out.println("received : " + message.typed(String.class).get());
    }
}
